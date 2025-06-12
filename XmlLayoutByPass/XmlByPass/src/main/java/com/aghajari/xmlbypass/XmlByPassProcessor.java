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

import com.aghajari.xmlbypass.attributes.AttrFactory;
import com.aghajari.xmlbypass.attributes.initial.Id;
import com.aghajari.xmlbypass.includer.IncludeFragment;
import com.aghajari.xmlbypass.includer.IncludeLayout;
import com.aghajari.xmlbypass.includer.IncludeSource;
import com.aghajari.xmlbypass.includer.IncludeViewStub;
import com.google.auto.service.AutoService;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class XmlByPassProcessor extends AbstractProcessor {

    private static final HashMap<Class<? extends IncludeSource>, IncludeSource> INC_SOURCES = new HashMap<>();

    static {
        INC_SOURCES.put(IncludeViewStub.class, new IncludeViewStub());
        INC_SOURCES.put(IncludeLayout.class, new IncludeLayout());
        INC_SOURCES.put(IncludeFragment.class, new IncludeFragment());
    }

    // android resource layouts path
    private static File layouts;

    // Map<LayoutFile, ClassName>
    private final HashMap<String, String> layoutMap = new HashMap<>();
    // Map<ClassName, Id>
    private final HashMap<String, String> layoutMap2 = new HashMap<>();
    // Layout classes that start with merge tag
    private final HashSet<String> mergeLayouts = new HashSet<>();

    // <include> may add extra layouts for translation
    private final ArrayList<String> extraLayouts = new ArrayList<>();

    private String resourcesDirPath;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        resourcesDirPath = processingEnv.getOptions().get("xmlByPassResDir");

        if (resourcesDirPath != null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "XmlByPass Resources directory: " + resourcesDirPath);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(XmlByPass.class.getCanonicalName());
        annotations.add(XmlByPassAttr.class.getCanonicalName());
        annotations.add(XmlByPassAttrs.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /*
     * We need to use process only once as we always
     * need list of attrs and layouts together
     * this variable helps us to apply process only once
     */
    private boolean done = false;

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (done)
            return true;
        done = true;

        ArrayList<XmlByPassAttr> attrs = new ArrayList<>();
        Set<? extends Element> elements;

        //AttrGenerator.test(processingEnv);

        // check for custom attrs
        elements = roundEnvironment.getElementsAnnotatedWith(XmlByPassAttr.class);
        for (Element element : elements)
            attrs.add(element.getAnnotation(XmlByPassAttr.class));

        elements = roundEnvironment.getElementsAnnotatedWith(XmlByPassAttrs.class);
        for (Element element : elements)
            attrs.addAll(Arrays.asList(element.getAnnotation(XmlByPassAttrs.class).value()));

        for (XmlByPassAttr attr : attrs)
            AttrFactory.include(attr);

        elements = roundEnvironment.getElementsAnnotatedWith(XmlByPass.class);
        ArrayList<String> styles = new ArrayList<>();
        ArrayList<XmlLayoutSaver> layoutSaver = new ArrayList<>();

        extraLayouts.clear();
        layoutMap.clear();
        layoutMap2.clear();
        INC_SOURCES.values().forEach(IncludeSource::detach);

        String packageName = null;
        XmlByPass base = null;
        findLayouts();

        boolean includeAll = false;

        // map all layouts that we need to convert
        for (Element element : elements) {
            XmlByPass xmlByPass = element.getAnnotation(XmlByPass.class);

            if (!xmlByPass.packageName().isEmpty())
                packageName = xmlByPass.packageName();

            if (base == null)
                base = xmlByPass;

            if (xmlByPass.layouts().length == 0)
                includeAll = true;

            for (XmlLayout xml : xmlByPass.layouts()) {
                if (xml == null)
                    continue;

                if (packageName == null) {
                    packageName = ((TypeElement) element).getQualifiedName().toString();
                    packageName = packageName.substring(0, packageName.lastIndexOf('.'));
                    applyPackageName(packageName);
                }

                if (xml.layout().equalsIgnoreCase("*")) {
                    includeAll = true;
                    continue;
                }

                if (layoutMap.containsKey(xml.layout().toLowerCase()))
                    error("Layout already exists in XmlByPass: " + xml.layout());

                File file = new File(xmlByPass.path().isEmpty() ?
                        layouts.getAbsolutePath() : xmlByPass.path(),
                        xml.layout() + ".xml");
                String className = xml.className().isEmpty() ? xml.layout() : xml.className();

                layoutMap.put(xml.layout().toLowerCase(), className);
                layoutSaver.add(new XmlLayoutSaver(xmlByPass, file, className, xml.parentClass(),
                        xmlByPass.viewModel() ? xml.viewModel() : null));
            }
        }

        if (includeAll) {
            File file = !layoutSaver.isEmpty() ? layoutSaver.get(0).file.getParentFile() : layouts;
            File[] files = file.listFiles();
            if (files != null)
                for (File f : files) {
                    if (f.getName().toLowerCase().endsWith(".xml")) {
                        String layout = f.getName().substring(0, f.getName().length() - 4);
                        if (layoutMap.containsKey(layout.toLowerCase()))
                            continue;

                        layoutMap.put(layout.toLowerCase(), layout);
                        layoutSaver.add(new XmlLayoutSaver(base, f, layout, "", base.viewModel() ? "" : null));
                    }
                }
        }

        // map layout ids, for a better usage in <include>
        for (XmlLayoutSaver saver : layoutSaver) {
            openXmlLayoutFirstTag(saver.file, (xpp) -> {
                if (xpp.getName().equalsIgnoreCase("merge")) {
                    mergeLayouts.add(saver.className);
                }

                String idValue = xpp.getAttributeValue(null, "android:id");
                if (idValue != null && idValue.startsWith("@"))
                    layoutMap2.put(saver.className, Id.getOriginalViewId(idValue));
            });
        }

        // convert xml to code :)
        for (XmlLayoutSaver saver : layoutSaver)
            generate(saver.file, saver.className, packageName, styles, saver.options, saver.parent, saver.viewModel);

        // convert extra layouts which previous layouts requested,
        // <include> needs this
        while (!extraLayouts.isEmpty()) {
            ArrayList<String> copy = new ArrayList<>(extraLayouts);
            extraLayouts.clear();

            for (String layout : copy) {
                File file = new File(layouts.getAbsolutePath(), layout + ".xml");
                String className = layoutMap.get(layout.toLowerCase());

                generate(file, className, packageName, styles, base, "", base == null || base.viewModel() ? "" : null);
            }
        }

        INC_SOURCES.values().forEach((s) -> s.write(this));

        // create styles if needed
        if (!styles.isEmpty()) {
            StringBuilder stylesRes = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n\n");
            for (String style : styles)
                stylesRes.append(style).append('\n');
            stylesRes.append("</resources>");
            try {
                FileObject f = processingEnv.getFiler().createSourceFile("XmlByPassStyle");
                File f2 = new File(f.toUri());
                File f3 = f2.getParentFile().getParentFile();
                String buildType = f3.getName();
                f3 = f3.getParentFile().getParentFile();
                File dir = new File(f3, "res/resValues/" + buildType + "/values");
                dir.mkdir();
                f3 = new File(dir, "xml_by_pass.xml");
                f3.createNewFile();
                PrintWriter pw = new PrintWriter(f3);
                pw.write(stylesRes.toString());
                pw.close();
                f.openWriter().append("/* XmlByPass by Aghajari */").close();
                f.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void applyPackageName(final String packageName) {
        INC_SOURCES.values()
                .forEach((s) -> s.setPackageName(packageName));
    }

    private void openXmlLayoutFirstTag(File file, XmlPullParserCodeBlock block) {
        XmlPullParser xpp = new KXmlParser();
        try {
            xpp.setInput(new FileReader(file));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    block.run(xpp);
                    break;
                }
                eventType = xpp.next();
            }
        } catch (Exception ignore) {
        }
    }

    @FunctionalInterface
    private interface XmlPullParserCodeBlock {
        void run(XmlPullParser xpp);
    }

    private static class XmlLayoutSaver {
        private final XmlByPass options;
        private final File file;
        private final String className;
        private final String parent;
        private final String viewModel;

        private XmlLayoutSaver(XmlByPass options, File file, String className, String parent, String viewModel) {
            this.options = options;
            this.file = file;
            this.className = className;
            this.parent = parent;
            this.viewModel = viewModel;
        }
    }

    private void generate(File file, String className, String packageName, ArrayList<String> styles,
                          XmlByPass xmlByPass, String parentClass, String viewModel) {
        SourceGenerator generator = new SourceGenerator(file, packageName, file.getName(), parentClass, viewModel, xmlByPass, this);
        generator.start(className);
        styles.addAll(generator.getStyles());

        write(generator.build(), packageName + "." + className);
        ViewModel model = generator.getViewModelGenerator();
        if (model != null) {
            String b = model.build();
            if (b != null)
                write(b, packageName + "." + model.getClassName());
        }
    }

    private void write(String src, String name) {
        try {
            JavaFileObject object = processingEnv.getFiler().createSourceFile(name);
            PrintWriter pw = new PrintWriter(object.openWriter());
            pw.write(src);
            pw.close();
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    public ProcessingEnvironment getProcessingEnv() {
        return processingEnv;
    }

    private void findLayouts() {
        if (layouts != null)
            return;

        if (resourcesDirPath != null) {
            layouts = new File(resourcesDirPath + "/layout");
            return;
        }

        Filer filer = processingEnv.getFiler();
        try {
            JavaFileObject dummySourceFile = filer.createSourceFile("XmlByPass");
            String dummySourceFilePath = dummySourceFile.toUri().toString();
            if (dummySourceFilePath.startsWith("file:")) {
                if (!dummySourceFilePath.startsWith("file://")) {
                    dummySourceFilePath = "file://" + dummySourceFilePath.substring("file:".length());
                }
            } else {
                dummySourceFilePath = "file://" + dummySourceFilePath;
            }
            URI cleanURI = new URI(dummySourceFilePath);
            File dummyFile = new File(cleanURI);
            File projectRoot = dummyFile.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
            dummySourceFile.openWriter().append("/* XmlByPass by Aghajari */").close();
            dummySourceFile.delete();
            layouts = new File(projectRoot.getAbsolutePath() + "/src/main/res/layout");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLayoutNameOrPutOnExtraLayouts(final String layoutName) {
        String layoutFileName = layoutName.toLowerCase();
        if (layoutMap.containsKey(layoutFileName)) {
            return layoutMap.get(layoutFileName);
        } else {
            final File file = new File(layouts.getAbsolutePath(), layoutFileName + ".xml");
            openXmlLayoutFirstTag(file, (xpp) -> {
                if (xpp.getName().equalsIgnoreCase("merge")) {
                    mergeLayouts.add(layoutName);
                }
            });

            extraLayouts.add(layoutFileName);
            layoutMap.put(layoutFileName, layoutName);
            return layoutName;
        }
    }

    public boolean isMergeLayout(String className) {
        return mergeLayouts.contains(className);
    }

    public boolean containsLayout(String className) {
        return layoutMap.containsValue(className);
    }

    public boolean containsLayoutFile(String fileName) {
        return layoutMap.containsKey(fileName.toLowerCase());
    }

    public String getIncludeSourceForPackage(
            Class<? extends IncludeSource> clz,
            String packageName
    ) {
        IncludeSource source = INC_SOURCES.get(clz);
        if (source == null) {
            error("Source " + clz.getName() + " not found!");
        }
        assert source != null;
        source.attach();
        layoutMap.putIfAbsent(source.getName().toLowerCase(), source.getName());
        return source.getNameForPackage(packageName);
    }

    public String getLayoutId(String className, String def) {
        return layoutMap2.getOrDefault(className, def);
    }

    public void error(String message) {
        RuntimeException exception = new RuntimeException(message);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, exception.getMessage());
        throw exception;
    }

    public void note(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }
}