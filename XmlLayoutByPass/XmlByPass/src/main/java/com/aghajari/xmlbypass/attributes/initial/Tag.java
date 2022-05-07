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

package com.aghajari.xmlbypass.attributes.initial;

import com.aghajari.xmlbypass.attributes.AttrValueParser;

/**
 * <code>tag(id, value)</code> parser
 */
public class Tag {

    public final String name;
    public int index;

    public Tag(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String parse(String id, String value) {
        id = AttrValueParser.parseRefId(id);

        if (value == null || value.equalsIgnoreCase("@null"))
            value = "null";
        else if (value.startsWith("@integer/") || value.startsWith("@android:integer/"))
            value = AttrValueParser.parseInteger(value);
        else if (value.startsWith("@color/") || value.startsWith("@android:color/")
                || (value.startsWith("#") && (value.length() == 7 || value.length() == 9)))
            value = AttrValueParser.parseColor(value);
        else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
            value = AttrValueParser.parseBoolean(value);
        else if (value.startsWith("@string/") || value.startsWith("@android:string/"))
            value = AttrValueParser.parseString(value);
        else {
            boolean parsed = false;
            try {
                int a = Integer.parseInt(value);
                parsed = true;
            } catch (Exception ignore) {
            }

            if (!parsed) {
                try {
                    float a = Float.parseFloat(value);
                    parsed = true;
                } catch (Exception ignore) {
                }
            }

            if (!parsed)
                value = AttrValueParser.parseString(value);
        }

        return "setTag(" + id + ", " + value + ")";
    }
}
