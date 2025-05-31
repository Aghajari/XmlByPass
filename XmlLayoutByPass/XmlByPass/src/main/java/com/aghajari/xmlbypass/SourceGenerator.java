/*
 * Copyright (C) 2022 - Amir Hossein Aghajari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.aghajari.xmlbypass;

import com.aghajari.xmlbypass.attributes.AttrValueParser;
import com.aghajari.xmlbypass.attributes.AttributesParser;
import com.aghajari.xmlbypass.attributes.AttrFactory;
import com.aghajari.xmlbypass.attributes.initial.Id;
import com.aghajari.xmlbypass.attributes.initial.Tag;
import com.aghajari.xmlbypass.attributes.initial.Theme;
import com.aghajari.xmlbypass.attributes.layoutparams.LayoutParamsAttributesParser;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class SourceGenerator {

    private final StringBuilder source = new StringBuilder();

    // shows the root class has created or not
    private boolean classCreated = false;

    // list of required imports
    private final List<String> imports = new ArrayList<>();

    // list of optional imports,
    // add unique imports at the end and also
    // you need to replace packages in source
    private final List<String> optionalImports = new ArrayList<>();

    // list of variables,
    // public variables are the one which have id already
    private final List<String> variables = new ArrayList<>();

    // list of functions
    // used to create initialize method for each qualifier
    private final List<String> functions = new ArrayList<>();

    // list of initialize methods (for each qualifier)
    private final List<String> functionsFinal = new ArrayList<>();

    // extra codes, such as some util functions
    private final Set<String> extraCodes = new HashSet<>();

    // styles for unknown attrs
    private final List<String> styles = new ArrayList<>();

    // index for writing imports at the end
    private final int indexForImport;
    // index for writing variables at the end
    private int indexForVariables;
    // index for writing initialize methods at the end
    private int indexForFunctions;
    // index for writing ViewModel
    private int indexForViewModel;

    // stack to store parents
    private final Stack<Parent> parents = new Stack<>();

    // stack to store indexes for tags with id & value
    private final Stack<Tag> tagIndexes = new Stack<>();

    // last tags info
    // used to find the previous view was a ViewGroup or not
    // so can add the data into parents stack
    private String lastOpenedClass = "", lastId = "";
    private static final int STATE_UNKNOWN = -1;
    private static final int STATE_OPEN = 0;
    private static final int STATE_CLOSE = 1;
    private int lastState = STATE_UNKNOWN;

    // Id and theme attr parser are different
    final Id id = new Id();
    final Theme theme = new Theme();

    // used to create unique name for each view
    private int unknownViewIndex = 1;

    private final String layoutName, parentClass, packageName, viewModel;
    //private String className;
    private final XmlByPass xmlByPass;
    private final File file;
    private final XmlByPassProcessor processor;

    private String qualifier = "";
    private ViewModel viewModelGenerator = null;

    public SourceGenerator(File layoutFile, String packageName, String layoutName, String parentClass,
                           String viewModel, XmlByPass xmlByPass, XmlByPassProcessor processor) {
        this.processor = processor;
        this.xmlByPass = xmlByPass;
        this.file = layoutFile;
        this.layoutName = layoutName.toLowerCase().replace(".xml", "");
        this.parentClass = parentClass;
        this.viewModel = viewModel;
        this.packageName = packageName;

        source.append("package ").append(packageName).append(";\n\n");

        indexForImport = source.length();
        importNew("android.content.Context");
        importNew("android.util.AttributeSet");

        if (!xmlByPass.R().isEmpty())
            importNew(xmlByPass.R() + ".R");
    }

    public void start(String className) {
        if (this.viewModel != null && (!viewModel.toLowerCase().equals("false")
                && !viewModel.toLowerCase().equals("null")))
            this.viewModelGenerator = new ViewModel(packageName, className, viewModel);

        //this.className = className;
        ArrayList<QualifierInfo> files = new ArrayList<>();
        files.add(new QualifierInfo("", file));


        // Find all qualifier layouts
        File parent = file.getParentFile().getParentFile();
        File[] listFiles = parent.listFiles();
        if (listFiles != null) {
            Arrays.sort(listFiles, (o, o2) -> {
                int a = o.getName().length() - o.getName().replaceAll("-", "").length();
                int b = o2.getName().length() - o2.getName().replaceAll("-", "").length();
                return Integer.compare(a, b);
            });

            for (File resFolder : listFiles) {
                if (resFolder.isDirectory() && resFolder.getName().startsWith("layout-")) {
                    File file2 = new File(resFolder, file.getName());
                    if (file2.exists())
                        files.add(new QualifierInfo(resFolder.getName().substring(7), file2));
                }
            }
        }
        if (files.size() > 1)
            importNew("android.content.res.Configuration");

        // all qualifiers must have same root view
        // and also views with same id must have same type
        String baseClass = null;
        for (QualifierInfo qualifierInfo : files) {
            qualifier = qualifierInfo.name;
            functions.clear();
            parents.clear();
            tagIndexes.clear();
            id.ids_saved.putAll(id.ids);
            id.ids.clear();
            unknownViewIndex = 1;

            try {
                XmlPullParser xpp = new KXmlParser();
                xpp.setInput(new FileReader(qualifierInfo.file));
                boolean firstClassLoaded = false;

                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = getRealName(xpp);

                        if (!isClassCreated()) {
                            addClass(className, baseClass = tagName, xpp, files);
                            firstClassLoaded = true;
                        } else if (!firstClassLoaded) {
                            firstClassLoaded = true;
                            if (!tagName.equals(baseClass)) {
                                processor.error("The parent of the layouts with " +
                                        "different qualifiers must be same type: " + baseClass + " or " + tagName);
                            }
                            parents.push(new Parent(tagName, "this"));
                            parseAttrs(xpp, true);
                        } else
                            parseAttrs(xpp);

                    } else if (eventType == XmlPullParser.END_TAG) {
                        endTag(getRealName(xpp));
                    }
                    eventType = xpp.next();
                }

                StringBuilder funcQ = new StringBuilder("\tprotected void init" +
                        (qualifier.isEmpty() ? "" : "_" + qualifier) + "() {\n");
                for (String func : functions)
                    funcQ.append(func).append('\n');
                funcQ.append("\t}\n");
                functionsFinal.add(0, funcQ.toString());

            } catch (Exception e) {
                processor.error(e.getMessage());
            }
        }
    }

    /**
     * @return true if current tag is fragment or FragmentContainerView,
     */
    private boolean isFragment(XmlPullParser xpp) {
        return xpp.getName().equalsIgnoreCase("fragment")
                || xpp.getName().equalsIgnoreCase("androidx.fragment.app.FragmentContainerView");
    }

    /**
     * Changes some basic tags
     *
     * <code>view</code> to it's class
     * <code>include</code> to {@link IncludeLayout}
     * <code>fragment</code> to {@link IncludeFragment}
     * <code>FragmentContainerView</code> to {@link IncludeFragment}
     * <p>
     * If {@link XmlByPass#include()} is active,
     * XmlByPass translates the internal layouts and uses its generated class
     */
    private String getRealName(XmlPullParser xpp) {
        if (xpp.getName().equals("view")) {
            String cls = xpp.getAttributeValue(null, "class");
            if (cls != null) {
                cls = cls.replace("$", ".");
                if (cls.startsWith("android.widget.")) {
                    String cls2 = cls.substring("android.widget.".length());
                    if (!cls2.contains("."))
                        return cls2;
                }

                return cls;
            }
        }

        if (xpp.getName().equals("include")) {
            String layout = xpp.getAttributeValue(null, "layout");
            if (layout != null) {
                if (layout.startsWith("@layout/")) {
                    if (xmlByPass.include() || processor.containsLayoutFile(layout.substring(8)))
                        return processor.getLayoutNameOrPutOnExtraLayouts(layout.substring(8));
                }

                processor.createIncludeLayout();
                return processor.getIncludeLayoutName(packageName);
            }
        }

        if (isFragment(xpp)) {
            processor.createIncludeFragment();
            return processor.getIncludeFragmentName(packageName);
        }
        return xpp.getName();
    }

    /**
     * Generates class structure and constructors
     */
    private void addClass(String className, String extend, XmlPullParser xpp, ArrayList<QualifierInfo> files) {
        classCreated = true;
        parents.push(new Parent(extend, "this"));

        source.append('\n');

        source.append("public class ").append(className).append(" extends ").append(extend).append(" {\n\n");
        indexForVariables = source.length();

        source.append("\n    public ").append(className).append("(Context context) {\n")
                .append("        this(context, null);\n")
                .append("    }\n")
                .append("\n")
                .append("    public ").append(className).append("(Context context, AttributeSet attrs) {\n")
                .append("        this(context, attrs, 0);\n")
                .append("    }\n")
                .append("\n")
                .append("    public ").append(className).append("(Context context, AttributeSet attrs, int defStyleAttr) {\n")
                .append("        super(context, attrs, defStyleAttr);\n");

        indexForViewModel = source.length();
        source.append('\n');

        if (files.size() == 1) {
            source.append("        init();\n");
        } else {
            boolean first = true;
            for (int i = files.size() - 1; i >= 0; i--) {
                QualifierInfo inf = files.get(i);
                if (i != 0)
                    source.append("        ").append(first ? "if (" : "else if (")
                            .append(inf.getCondition()).append(")\n");
                else
                    source.append("        else\n");

                source.append("        \t").append("init")
                        .append(inf.name.isEmpty() ? "" : "_" + inf.name).append("();\n");
                first = false;
            }
        }
        source.append("    }\n\n");
        indexForFunctions = source.length();

        parseAttrs(xpp, true);
    }

    // Might be useful in future
    private void resetTag() {
        // id.resetTag();
    }

    /**
     * Parses attributes
     */
    private void parseAttrs(XmlPullParser xpp) {
        parseAttrs(xpp, false);
    }

    private void parseAttrs(XmlPullParser xpp, boolean thisClass) {
        // check if it's only a tag or requestFocus for previous view
        if (xpp.getName().equalsIgnoreCase("tag")) {
            String id = xpp.getAttributeValue(null, "android:id"),
                    value = xpp.getAttributeValue(null, "android:value");

            String out = null;
            Tag tag = tagIndexes.peek();

            if (viewModelGenerator != null) {
                out = viewModelGenerator.parse(id, value, processor, tag.name);
                System.out.println(out);
                if (out != null && out.isEmpty())
                    return;
            }

            String src = "\t\t" + (out == null ? tag.name + "." + tag.parse(id, value) : out) + ";\n";
            source.insert(tag.index, src);
            tag.index += src.length();
            return;
        } else if (xpp.getName().equalsIgnoreCase("requestFocus")) {
            Tag tag = tagIndexes.peek();
            String src = "\t\t" + tag.name + "." + "requestFocus()" + ";\n";
            source.insert(tag.index, src);
            tag.index += src.length();
            return;
        }

        String tagName = getRealName(xpp);

        // if previous tag hasn't closed yet, it's a view group
        // add the new view group to stack
        if (lastState == STATE_OPEN) {
            parents.push(new Parent(lastOpenedClass, lastId));
        } else {
            lastState = STATE_OPEN;
        }
        lastOpenedClass = tagName;

        // notifies previous view is ready
        resetTag();

        // imports android.{widget/view}.{VIEW} as required
        // and other types as optional imports
        checkForImport(tagName);

        // stores all attributes in a map
        // AttributesParser must remove the attr
        // which parsed from this map
        // so at the end we can find the unknown attrs
        // and create an style for them.
        // only works if XmlByParser#styleable() be true
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < xpp.getAttributeCount(); i++)
            if (xpp.getAttributeName(i) != null && xpp.getAttributeValue(i) != null) {
                String name = xpp.getAttributeName(i);
                if (!name.startsWith("android:") &&
                        !AttrFactory.parsers.containsKey(name)
                        && name.contains(":")
                        && !xmlByPass.styleable()) {
                    String name2 = "android:" + name.substring(name.indexOf(':') + 1);
                    // lets try our chance with default parsers :)
                    if (AttrFactory.parsers.containsKey(name2))
                        name = name2;
                }

                map.put(name, xpp.getAttributeValue(i));
            }

        String mId = "this";
        boolean q = false;

        // Finds a unique name for this view
        if (!thisClass) {
            if (id.supports(map)) {
                try {
                    mId = id.getViewId(map, tagName);
                } catch (Exception e) {
                    processor.error(e.getMessage());
                }
                addNewVariable(tagName, mId, true);
            } else {
                String unique = "view" + (unknownViewIndex++) + (qualifier.isEmpty() ? "" : "_" + qualifier);
                mId = id.getUniqueViewId(tagName, processor.getLayoutId(tagName, unique));
                q = mId.equals(unique);
                addNewVariable(tagName, mId, !q);
            }
        }
        lastId = mId;

        // Creates a unique function to create this view
        String funcName = "init" + mId.substring(0, 1).toUpperCase() + mId.substring(1)
                + (q || qualifier.isEmpty() ? "" : "_" + qualifier);

        addFunction(funcName, map);

        // Stores index of current line,
        // We need to instantiate at the end
        // to decide we need custom style/theme or not
        int createInstanceIndex = source.length();

        // <include> tag is a little different
        if (map.containsKey("layout") &&
                xpp.getName().equals("include") &&
                tagName.endsWith(IncludeLayout.getName())) {
            String layoutId = null;
            String layout = map.get("layout");

            if (layout.startsWith("@layout/"))
                layoutId = "R.layout." + AttrValueParser.getAnyResName(layout.substring(8));
            else if (layout.startsWith("@android:layout/"))
                layoutId = "android.R.layout." + AttrValueParser.getAnyResName(layout.substring(16));

            if (layoutId != null)
                addAttr(mId, "loadLayout(" + layoutId + ")", true);
        }

        // Adds some util functions if needed
        resolveAttr(map);

        // AttributesParser will remove the solved attrs from map
        // So we need a copy of the map for iterating
        HashMap<String, String> copyMap = new HashMap<>(map);
        for (String key : copyMap.keySet()) {
            // finds correct parser for this attr
            AttributesParser parser = AttrFactory.parsers.get(key);
            if (parser == null && id.supports(map))
                parser = id;

            if (parser != null && parser.supports(map, tagName)) {
                String[] p = parser.parse(map, mId);
                if (p != null && p.length > 0) {
                    if (parser.needsToAddIdAtFirst(this)) {
                        for (String p2 : p)
                            addAttr(mId, p2, parser.needsSemicolon());
                    } else {
                        for (String p2 : p)
                            addAttr(p2, parser.needsSemicolon());
                    }
                }

                p = parser.imports();
                if (p != null && p.length > 0) {
                    for (String p2 : p)
                        importNew(p2);
                }
            }
        }

        // XmlParser will find tags with id & value after attrs,
        // So we need to add index of this line to stack
        // later if a tag included, we will know where to insert it
        tagIndexes.push(new Tag(mId, source.length()));

        // fragment is a little different
        if (isFragment(xpp) &&
                tagName.endsWith(IncludeFragment.getName())) {
            String cls;
            if (map.containsKey("android:name"))
                cls = map.get("android:name").trim();
            else
                cls = map.get("class").trim();
            cls = cls.replace("$", ".");

            addAttr(mId, "showFragment(" + cls + ".class)", true);
        }

        // time to generate a layout params for this view
        // android:layout_width and android:layout_height are necessary for each view
        // Only <include> can ignore these attrs!
        if (map.containsKey("android:layout_width") && map.containsKey("android:layout_height")) {
            LayoutParamsAttributesParser lp =
                    AttrFactory.findBestLayoutParamsParser(mId, thisClass && !parentClass.isEmpty() ? parentClass :
                            parents.lastElement().className, map);
            String[] p = lp.parse(map);
            if (p != null && p.length > 0) {
                String var;
                if (p.length == 1) {
                    // Converts
                    // LayoutParams lp = new LayoutParams(..)
                    // parent.addView(view, lp)
                    // To
                    // parent.addView(view, new LayoutParams(..))
                    var = p[0].substring(p[0].indexOf('=') + 1).trim();
                } else {
                    var = lp.getVarName();
                    lp.needsToAddIdAtFirst(this); // generate extra code if needed
                    for (String p2 : p)
                        addAttr(p2, lp.needsSemicolon());
                }

                if (thisClass)
                    addAttr(mId, "setLayoutParams(" + var + ")", true);
                else
                    addAttr(parents.lastElement().id, "addView(" + mId + ", " + var + ")", true);
            }
            p = lp.imports();
            if (p != null && p.length > 0) {
                for (String p2 : p)
                    importNew(p2);
            }
        } else { // <include> :)
            addAttr(parents.lastElement().id, "addView(" + mId + ")", true);
        }

        // these attrs are internal
        map.remove("class");
        map.remove("android:name");
        map.remove("layout");
        if (!thisClass)
            createView(mId, false, tagName, map,
                    checkForStyles(mId, map), createInstanceIndex);

        closeFunction();
    }

    /**
     * Creates style for unknown attrs
     */
    private boolean checkForStyles(String viewName, HashMap<String, String> map) {
        if (!xmlByPass.styleable() || map.isEmpty())
            return false;

        String parent = "";
        if (theme.supports(map)) {
            String p = theme.getThemStyleName(map);
            if (p != null && p.length() > 0)
                parent = " parent=\"" + p + "\"";
        }

        int count = 0;
        StringBuilder builder = new StringBuilder(
                "\t<style name=\"" + layoutName + "_" + viewName + "\"" + parent + ">\n");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String k = entry.getKey();
            if (k.startsWith("tools:") || k.startsWith("xmlns:")
                    || k.equalsIgnoreCase("android:theme")
                    || k.equalsIgnoreCase("style"))
                continue;
            if (!k.startsWith("android:") && k.contains(":"))
                k = k.substring(k.indexOf(':') + 1);

            builder.append("\t\t<item name=\"").append(k).append("\">")
                    .append(entry.getValue()).append("</item>\n");
            count++;
        }
        builder.append("\t</style>\n");
        if (count > 0)
            styles.add(builder.toString());

        return count > 0;
    }

    private void addFunction(String name, HashMap<String, String> map) {
        // add @TargetApi
        if (map.containsKey("tools:targetApi")) {
            importNew("android.annotation.TargetApi");
            String value = map.get("tools:targetApi");
            try {
                Integer.parseInt(value);
            } catch (Exception ignore) {
                value = "android.os.Build.VERSION_CODES." + value.toUpperCase();
            }

            source.append("\t").append("@TargetApi(").append(value).append(")\n");
            map.remove("tools:targetApi");
        }

        source.append("\tprotected void ").append(name).append("() {\n");

        String func = "\t\t" + name + "();";
        if (!functions.contains(func))
            functions.add(func);
    }

    /**
     * Instantiates a view
     */
    @SuppressWarnings("SameParameterValue")
    private void createView(String id, boolean newObject, String className,
                            HashMap<String, String> map, boolean style, int index) {
        String context = "getContext()";
        String styleId = "";

        if (style) {
            context = theme.parse(layoutName + "_" + id, map);
            importNew(theme.imports()[0]);
            styleId = ", null, R.style." + layoutName + "_" + id;
        } else if (map.containsKey("style")) {
            styleId = ", null, " + theme.getStyleId(map);
            context = theme.parse(map)[0];
            importNew(theme.imports()[0]);
        } else if (theme.supports(map)) {
            context = theme.parse(map)[0];
            importNew(theme.imports()[0]);
        }

        String src;
        if (newObject)
            src = className + " " + id + " = new " + className + "(" + context + styleId + ")";
        else
            src = id + " = new " + className + "(" + context + styleId + ")";

        src = "\t\t" + src + ";\n";
        tagIndexes.peek().index += src.length();
        source.insert(index, src);
    }

    private void addAttr(String src, boolean semicolon) {
        source.append("\t\t").append(src).append(semicolon ? ";\n" : '\n');
    }

    private void addAttr(String id, String src, boolean semicolon) {
        source.append("\t\t").append(id).append('.').append(src).append(semicolon ? ";\n" : '\n');
    }

    private void closeFunction() {
        source.append("\t}\n\n");
    }

    private void addNewVariable(String tagName, String id, boolean isPublic) {
        String var = "\t" + (isPublic ? "public" : "protected") + " " + tagName + " " + id + ";";
        if (!variables.contains(var))
            variables.add(0, var);
    }

    /**
     * android.widget.View.SubView to View
     */
    static String getClassName(String fullName) {
        String[] packages = fullName.split("\\.");
        int a = 0;
        for (String p : packages) {
            a++;
            if (Character.isUpperCase(p.charAt(0)))
                return a > 1 ? p : null;
        }
        return null;
    }

    // android.widget.View to android.widget
    /*static String getPackageName(String fullName) {
        String[] packages = fullName.split("\\.");
        StringBuilder out = new StringBuilder();
        for (String p : packages) {
            if (Character.isUpperCase(p.charAt(0)))
                return out.length() > 0 ? out.substring(0, out.length() - 1) : null;
            else
                out.append(p).append('.');
        }
        return null;
    }*/

    /**
     * android.widget.View.SubClass to android.widget.View
     */
    static String getFullName(String full) {
        String[] packages = full.split("\\.");
        StringBuilder im = new StringBuilder();
        for (String p : packages) {
            if (Character.isLowerCase(p.charAt(0)))
                im.append(p).append(".");
            else {
                if (im.length() == 0)
                    return null;

                im.append(p);
                return im.toString();
            }
        }
        return null;
    }

    private void importNew(String im) {
        String im2 = im.trim();
        if (!imports.contains(im2))
            imports.add(0, getFullName(im2));
    }

    private void importEnd(String im) {
        String im2 = im.trim();
        if (!optionalImports.contains(im2))
            optionalImports.add(0, im2);
    }

    private void checkForImport(String name) {
        if (processor.containsLayout(name))
            return;

        if (name.equals("View") ||
                name.equals("TextureView") ||
                name.equals("SurfaceView") ||
                name.equals("ViewStub")) /* Do we really need ViewStub? :/ */
            importNew("android.view." + name);
        else if (!name.contains("."))
            importNew("android.widget." + name);
        else {
            try {
                String im = getFullName(name);
                if (im != null)
                    importEnd(im);
            } catch (Exception ignore) {
            }
        }
    }

    private void endTag(String className) {
        if (className.equalsIgnoreCase("tag")
                || className.equalsIgnoreCase("requestFocus"))
            return;
        tagIndexes.pop();
        lastState = STATE_CLOSE;

        if (parents.lastElement().className.equals(className))
            parents.pop();
    }

    /**
     * @return true if first tag (root view) has been read
     */
    public boolean isClassCreated() {
        return classCreated;
    }

    /**
     * Adds an extra code (function) at the end of source
     */
    public void addExtraCode(String src) {
        extraCodes.add(src);
    }

    /**
     * {@link XmlByPassProcessor} may need to create style for unknown attributes
     */
    public List<String> getStyles() {
        return styles;
    }

    /**
     * ViewModel need this function for observer
     */
    private void resolveOwner() {
        importNew("android.content.ContextWrapper");
        importNew("android.content.Context");
        importNew("androidx.lifecycle.LifecycleOwner");
        importNew("androidx.lifecycle.ViewModelStoreOwner");

        addExtraCode("    protected LifecycleOwner getOwner() {\n" +
                "        Context result = getContext();\n" +
                "        while (result instanceof ContextWrapper) {\n" +
                "            if (result instanceof LifecycleOwner)\n" +
                "                return (LifecycleOwner) result;\n" +
                "            result = ((ContextWrapper) result).getBaseContext();\n" +
                "        }\n" +
                "        throw new RuntimeException(\"No LifecycleOwner exists!\");\n" +
                "    }\n");

        addExtraCode("    protected ViewModelStoreOwner getViewModelOwner() {\n" +
                "        Context result = getContext();\n" +
                "        while (result instanceof ContextWrapper) {\n" +
                "            if (result instanceof ViewModelStoreOwner)\n" +
                "                return (ViewModelStoreOwner) result;\n" +
                "            result = ((ContextWrapper) result).getBaseContext();\n" +
                "        }\n" +
                "        throw new RuntimeException(\"No ViewModelStoreOwner exists!\");\n" +
                "    }\n");
    }

    /**
     * Adds some functions if needed,
     *
     * <code>resolveAttribute</code> to get resource id from <code>?attr</code>
     * <code>dp</code>, <code>sp</code> and etc.. to apply dimensions
     */
    private void resolveAttr(HashMap<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String v = entry.getValue().toLowerCase().trim();

            if (AttrValueParser.isAttrValue(v)) {
                importNew("android.util.TypedValue");

                addExtraCode("    private int resolveAttribute(int attrId){\n" +
                        "        TypedValue outValue = new TypedValue();\n" +
                        "        getContext().getTheme().resolveAttribute(attrId, outValue, true);\n" +
                        "        return outValue.resourceId;\n" +
                        "    }\n");
            } else if (AttrValueParser.isDimensionValue(v)) {
                importNew("android.util.TypedValue");
                String unit = AttrValueParser.getUnit(v);
                String funcName = v.endsWith("dip") ? "dp" : v.substring(v.length() - 2);
                if (funcName.equals("sp") && entry.getKey().equalsIgnoreCase("android:textSize"))
                    continue;

                if (funcName.equals("dp")) {
                    addExtraCode("    private int " + funcName + "(float value) {\n" +
                            "        return (int) TypedValue.applyDimension(TypedValue." + unit + ",\n" +
                            "                value, getResources().getDisplayMetrics());\n" +
                            "    }\n");
                } else {
                    addExtraCode("    private float " + funcName + "(float value) {\n" +
                            "        return TypedValue.applyDimension(TypedValue." + unit + ",\n" +
                            "                value, getResources().getDisplayMetrics());\n" +
                            "    }\n");
                }
            }
        }
    }

    public ViewModel getViewModelGenerator() {
        return viewModelGenerator;
    }

    // internal attributes have used these classes
    private void importInternal() {
        importEnd("androidx.core.content.res.ResourcesCompat");
        importEnd("androidx.recyclerview.widget.StaggeredGridLayoutManager");
        importEnd("androidx.recyclerview.widget.LinearLayoutManager");
        importEnd("androidx.recyclerview.widget.GridLayoutManager");
        importEnd("android.graphics.drawable.ColorDrawable");
        importEnd("android.content.res.ColorStateList");
        importEnd("android.graphics.PorterDuff");
        importEnd("android.graphics.Typeface");
        importEnd("android.util.TypedValue");
        importEnd("android.os.Build");
        importEnd("android.os.LocaleList");
        importEnd("android.text.TextUtils");
        importEnd("android.text.InputType");
        importEnd("android.text.InputFilter");
        importEnd("android.widget.ArrayAdapter");
        importEnd("android.widget.ImageView");
        importEnd("android.view.Gravity");
        importEnd("android.view.ViewOutlineProvider");
        importEnd("java.util.Locale");

        // android.widget.TextView required
        // com.aghajari.TextView optional
        // TextView = TextView, so we can not import the optional one!

        // add optional imports
        // we need to make sure there is no duplicated className

        loop:
        for (String end : optionalImports) {
            String cls = getClassName(end);
            if (cls == null)
                continue;

            for (String im : imports) {
                String cls2 = getClassName(im);
                if (cls.equals(cls2))
                    continue loop;
            }
            if (source.toString().contains(end))
                importNew(end);
        }

        // sort imports based on packages
        imports.sort(Comparator.reverseOrder());
    }


    // and it's done
    // burn baby burn :D
    public String build() {
        importInternal();

        if (viewModelGenerator != null && viewModelGenerator.needsViewModel()) {
            importNew("androidx.lifecycle.ViewModelProvider");
            resolveOwner();
            String src = "\t\tviewModel = new ViewModelProvider(getViewModelOwner()).get("
                    + viewModelGenerator.getClassName() + ".class);\n";
            source.insert(indexForViewModel, src);
            indexForFunctions += src.length();
            variables.add("\tpublic " + viewModelGenerator.getClassName() + " viewModel;");
        }

        // used to break line whenever package/type changed
        String last = "";
        for (String im : imports) {
            im = "import " + im + ";\n";
            if (im.contains(".")) {
                String first = im.substring(0, im.indexOf('.'));
                if (!last.isEmpty() && !last.equals(first))
                    im = im + "\n";
                last = first;
            }

            source.insert(indexForImport, im);
            indexForVariables += im.length();
            indexForFunctions += im.length();
        }

        // sort public variables on top
        variables.sort((a, b) -> a.substring(0, b.indexOf(' ')).compareTo(b.substring(0, a.indexOf(' '))));
        last = "";
        for (String var : variables) {
            String first = var.substring(0, var.indexOf(' '));
            if (!last.isEmpty() && !last.equals(first))
                var = var + "\n";
            last = first;

            source.insert(indexForVariables, var + "\n");
            indexForFunctions += var.length() + 1;
        }

        for (String func : functionsFinal) {
            source.insert(indexForFunctions, func + "\n");
        }

        for (String extra : extraCodes) {
            source.append(extra).append('\n');
        }

        source.append("}");
        String src = source.toString();

        // replace imported classes
        for (String im : imports) {
            String full = getFullName(im);
            String className = getClassName(im);
            if (full != null && className != null) {
                try {
                    src = src.replaceAll(
                            full.replaceAll("\\.", "\\\\.") + "(?=[^a-zA-Z0-9;])"
                            , className);
                } catch (Exception ignore) {
                }
            }
        }
        return src;
    }

    // used to store list of qualifiers on start
    private static class QualifierInfo {
        public final String name;
        private final String orgName;
        public final File file;

        private QualifierInfo(String name, File file) {
            this.orgName = name.toLowerCase();
            this.name = name.replace("-", "_").toLowerCase();
            this.file = file;
        }

        // must merge conditions using logical and, for example
        // layout-land-v21 means device sdk version must be >= 21
        // and be on landscape mode
        // return "true" for unknown states (hopefully this will never happen)
        public String getCondition() {
            if (orgName.isEmpty())
                return "true";

            if (orgName.contains("-")) {
                String[] names = orgName.split("-");
                StringBuilder condition = new StringBuilder();
                for (String n : names) {
                    String a = Qualifiers.parseCondition(n);
                    if (!a.isEmpty() && !a.equals("true"))
                        condition.append(a).append(" && ");
                }
                String out = condition.toString().trim();
                return out.substring(0, out.length() - 2).trim();
            } else {
                return Qualifiers.parseCondition(orgName);
            }
        }
    }

    // used to store parent id & className on parents stack
    private static class Parent {
        private final String className;
        private final String id;

        private Parent(String className, String id) {
            this.className = className;
            this.id = id;
        }
    }

}
