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

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import java.io.PrintWriter;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

/**
 * Auto Generates xml attrs to {@link com.aghajari.xmlbypass.attributes.Attr}
 */
class AttrGenerator {

    public static void test(ProcessingEnvironment env) {
        XmlPullParser xpp = new KXmlParser();
        try {
            xpp.setInput(new StringReader(getString()));
            int eventType = xpp.getEventType();

            boolean isAttr = false;
            String name = "", format = "";
            LinkedHashMap<String, String> enums = new LinkedHashMap<>();

            String src = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("attr")) {
                        name = xpp.getAttributeValue(null, "name");

                        if (AttrFactory.parsers.containsKey("android:" + name)) {
                            name = "";
                            isAttr = false;
                        } else {
                            isAttr = true;
                            if (xpp.getAttributeCount() > 1)
                                format = xpp.getAttributeValue(null, "format");
                        }

                    } else if (xpp.getName().equals("flag") || xpp.getName().equals("enum")) {
                        if (isAttr)
                            enums.put(xpp.getAttributeValue(0), xpp.getAttributeValue(1));
                    }
                } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("attr")) {
                    if (isAttr) {
                        String enumsStr = "";
                        if (!enums.isEmpty()) {
                            for (Map.Entry<String, String> entry : enums.entrySet()) {
                                enumsStr += ", new AttrEnum(\"" + entry.getKey() + "\", \""
                                        + entry.getValue() + "\")";
                            }
                        }
                        src += ((format == null || format.isEmpty()) && enums.isEmpty() ? "/*OOPS*/ " : "")
                                + "new Attr(\"" + name + "\", \"" +
                                format + "\"" + enumsStr + "),\n";
                    }

                    isAttr = false;
                    enums.clear();
                    name = "";
                    format = "";
                }
                eventType = xpp.next();
            }

            JavaFileObject j = env.getFiler().createSourceFile("attrs");
            PrintWriter w = new PrintWriter(j.openWriter());
            w.write(src);
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getString() {
        return "        <!-- Background color for CardView. -->\n" +
                "        <attr format=\"color\" name=\"cardBackgroundColor\"/>\n" +
                "        <!-- Corner radius for CardView. -->\n" +
                "        <attr format=\"dimension\" name=\"cardCornerRadius\"/>\n" +
                "        <!-- Elevation for CardView. -->\n" +
                "        <attr format=\"dimension\" name=\"cardElevation\"/>\n" +
                "        <!-- Maximum Elevation for CardView. -->\n" +
                "        <attr format=\"dimension\" name=\"cardMaxElevation\"/>\n" +
                "        <!-- Add padding in API v21+ as well to have the same measurements with previous versions. -->\n" +
                "        <attr format=\"boolean\" name=\"cardUseCompatPadding\"/>\n" +
                "        <!-- Add padding to CardView on v20 and before to prevent intersections between the Card content and rounded corners. -->\n" +
                "        <attr format=\"boolean\" name=\"cardPreventCornerOverlap\"/>\n" +
                "        <!-- Inner padding between the edges of the Card and children of the CardView. -->\n" +
                "        <attr format=\"dimension\" name=\"contentPadding\"/>\n" +
                "        <!-- Inner padding between the left edge of the Card and children of the CardView. -->\n" +
                "        <attr format=\"dimension\" name=\"contentPaddingLeft\"/>\n" +
                "        <!-- Inner padding between the right edge of the Card and children of the CardView. -->\n" +
                "        <attr format=\"dimension\" name=\"contentPaddingRight\"/>\n" +
                "        <!-- Inner padding between the top edge of the Card and children of the CardView. -->\n" +
                "        <attr format=\"dimension\" name=\"contentPaddingTop\"/>\n" +
                "        <!-- Inner padding between the bottom edge of the Card and children of the CardView. -->\n" +
                "        <attr format=\"dimension\" name=\"contentPaddingBottom\"/>";
    }
}
