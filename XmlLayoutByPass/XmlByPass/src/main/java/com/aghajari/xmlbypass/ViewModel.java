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

import com.aghajari.xmlbypass.attributes.Attr;
import com.aghajari.xmlbypass.attributes.AttrFactory;
import com.aghajari.xmlbypass.attributes.AttributesParser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * ViewModel is a class that is responsible for preparing and managing the data...
 * Helps you to implement MVVM architecture easy A!
 * Example:
 * <pre>
 *    {@literal <}tag android:id={@literal "@}+id/myText" android:value="live;type=string;Hello World!"/>
 *    {@literal <}tag android:id={@literal "@}+id/myInt" android:value="live;type=int"/>
 * </pre>
 * Will generate
 * <pre>
 *     public class ViewModelClassName extends ViewModel {
 *          MutableLiveData<String> myText = new MutableLiveData<>("Hello World!");
 *          MutableLiveData<Integer> myInt = new MutableLiveData<>();
 *      ...
 * </pre>
 * <p>
 * And you can use live variables just like this:
 * <pre>
 *    {@literal <}TextView
 *        android:id={@literal @"}+id/tv"
 *        android:layout_width="wrap_content"
 *        android:layout_height="wrap_content">
 *        {@literal <}tag android:id="@id/myText" android:value="live;attr=text"/>
 *    {@literal <}/TextView>
 * </pre>
 * The generated code for {@code TextView} will be like :
 * <pre>
 *     viewModel.getMyText().observe(getOwner(), myText -> tv.setText(myText));
 * </pre>
 * <p>
 * Other ways to do this:
 * <pre>
 *     {@literal <}tag android:id="@id/myText" android:value="live;func=setText"/>
 *      OR
 *     {@literal <}tag android:id="@id/myText" android:value="live;src=setText(myText +{@literal &quot;} :) {@literal &quot;})"/>
 * </pre>
 */
class ViewModel {

    private final String packageName;
    private final String className;

    private final Map<String, LiveDataVar> variables = new LinkedHashMap<>();
    private final List<String> imports = new ArrayList<>();
    private final List<String> optionalImports = new ArrayList<>();

    public ViewModel(String packageName, String className, String cls) {
        this.packageName = packageName;
        if (cls.isEmpty()) {
            if (className.contains("_") || className.toLowerCase().equals(className)) {
                cls = className + "_model";
            } else {
                cls = className + "ViewModel";
            }
        }
        this.className = cls;

        imports.add("androidx.lifecycle.LiveData");
        imports.add("androidx.lifecycle.MutableLiveData");
        imports.add("androidx.lifecycle.ViewModel");
    }

    public String getClassName() {
        return className;
    }

    public String parse(String id, String value, XmlByPassProcessor processor, String tag) {
        if (!value.toLowerCase().startsWith("live;"))
            return null;
        if (!value.endsWith(";"))
            value += ";";

        if (id.toLowerCase().startsWith("@+id/"))
            id = id.substring(5);
        else if (id.toLowerCase().startsWith("@id/"))
            id = id.substring(4);

        String format = null;
        String attr = null, func = null;
        String _src = null;
        String src = value.substring(5);

        if (variables.containsKey(id.trim())) {
            format = variables.get(id.trim()).format;
            attr = variables.get(id.trim()).attr;
        }

        loop:
        while (src.contains(";")) {
            int index = src.indexOf(';');
            String parse = src.substring(0, index);
            if (parse.contains("=")) {
                String[] a = parse.split("=");
                if (a.length != 2)
                    break;

                switch (a[0].toLowerCase().trim()) {
                    case "format":
                    case "type":
                        format = a[1].trim();
                        break;
                    case "src":
                    case "source":
                    case "code":
                    case "line":
                        _src = a[1].trim();
                        break;
                    case "func":
                    case "function":
                    case "method":
                        func = a[1].trim();
                        break;
                    case "attr":
                    case "attribute":
                        attr = a[1].trim();
                        String contains = null;
                        if (AttrFactory.parsers.containsKey(attr))
                            contains = attr;
                        else if (AttrFactory.parsers.containsKey("android:" + attr))
                            contains = "android:" + attr;

                        if (contains != null) {
                            if (format == null) {
                                AttributesParser parser = AttrFactory.parsers.get(contains);
                                if (parser instanceof Attr) {
                                    if (((Attr) parser).format != null && !((Attr) parser).format.isEmpty())
                                        format = ((Attr) parser).format;
                                }
                            }
                        } else {
                            processor.error(value + ": " + attr + " doesn't exists!");
                        }
                        break;
                    default:
                        break loop;
                }
            } else
                break;
            src = src.substring(index + 1);
        }

        if (format == null) {
            processor.note(value + " ignored, couldn't find the format");
            return null;
        } /*else if (attr == null && func == null) {
            processor.note(value + " ignored, couldn't find the attr");
            return false;
        }*/

        switch (format.toLowerCase()) {
            case "bool":
            case "boolean":
                format = "Boolean";
                break;
            case "string":
                format = "String";
                break;
            case "double":
                format = "Double";
                break;
            case "float":
                format = "Float";
                break;
            case "int":
            case "integer":
                format = "Integer";
                break;
            case "long":
                format = "Long";
                break;
            case "char":
            case "character":
                format = "Character";
                break;
            case "byte":
                format = "Byte";
                break;
            default:
                if (format.contains(".")) {
                    String str = SourceGenerator.getFullName(format);
                    if (!optionalImports.contains(str))
                        optionalImports.add(str);
                }
        }

        if (src.length() > 0)
            src = src.substring(0, src.length() - 1);

        id = id.trim();
        if (!variables.containsKey(id))
            variables.put(id, new LiveDataVar(id, format, attr, src.trim()));

        if (attr != null || func != null || _src != null) {
            if (func == null && _src == null)
                func = "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);

            if (_src == null)
                _src = func + "(" + id + ")";

            String name = id;
            if (!Character.isUpperCase(name.charAt(0)))
                name = name.substring(0, 1).toUpperCase() + name.substring(1);

            return "viewModel.get" + name + "().observe(getOwner(), " + id + " -> " + tag + "." + _src + ")";
        }

        return "";
    }

    private void importInternal() {
        loop:
        for (String end : optionalImports) {
            String cls = SourceGenerator.getClassName(end);
            if (cls == null)
                continue;

            for (String im : imports) {
                String cls2 = SourceGenerator.getClassName(im);
                if (cls.equals(cls2))
                    continue loop;
            }
            if (!imports.contains(end))
                imports.add(end);
        }

        imports.sort(String::compareTo);
    }

    public boolean needsViewModel() {
        return variables.size() > 0;
    }

    public String build() {
        if (variables.size() == 0)
            return null;

        StringBuilder source = new StringBuilder("package " + packageName + ";\n\n");
        importInternal();

        String last = "";
        for (String im : imports) {
            im = "import " + im + ";\n";
            if (im.contains(".")) {
                String first = im.substring(0, im.indexOf('.'));
                if (!last.isEmpty() && !last.equals(first))
                    im = "\n" + im;
                last = first;
            }

            source.append(im);
        }

        source.append('\n');
        source.append("public class ").append(className).append(" extends ViewModel {\n\n");

        for (LiveDataVar var : variables.values()) {
            source.append("\tMutableLiveData<").append(var.format).append("> ")
                    .append(var.name).append(" = new MutableLiveData<>(")
                    .append(var.value).append(");\n");
        }
        source.append('\n');


        for (LiveDataVar var : variables.values()) {
            String name = var.name;
            if (!Character.isUpperCase(name.charAt(0)))
                name = name.substring(0, 1).toUpperCase() + name.substring(1);

            source.append("\tpublic LiveData<").append(var.format).append("> get").append(name).append("() {\n");
            source.append("\t\treturn ").append(var.name).append(";\n\t}\n\n");

            source.append("\tpublic void ").append("set").append(name).append("(")
                    .append(var.format).append(" value) {\n");
            source.append("\t\t").append(var.name).append(".setValue(value);\n\t}\n\n");
        }

        source.append('}');
        String src = source.toString();
        for (String im : imports) {
            String full = SourceGenerator.getFullName(im);
            String className = SourceGenerator.getClassName(im);
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

    private static class LiveDataVar {
        private final String name, format, attr, value;

        LiveDataVar(String name, String format, String attr, String value) {
            this.name = name;
            this.format = format;
            this.attr = attr;

            if (format.equalsIgnoreCase("string") && !value.isEmpty())
                this.value = "\"" + value + "\"";
            else if (format.equalsIgnoreCase("float")) {
                if (!value.toLowerCase().endsWith("f"))
                    value += "f";
                this.value = value;
            } else {
                this.value = value;
            }
        }
    }
}
