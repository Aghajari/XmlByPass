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

package com.aghajari.xmlbypass.attributes.text;

import com.aghajari.xmlbypass.attributes.AttrValueParser;
import com.aghajari.xmlbypass.attributes.AttributesParser;

import java.util.HashMap;

/**
 * Parser of
 * <code>android:textStyle</code>,
 * <code>android:typeface</code>,
 * <code>android:fontFamily</code>
 */
public class Typeface implements AttributesParser {

    /*
            new Attr("fontFamily", "string"),
            new Attr("typeface", "",
                    new AttrEnum("normal", "0"),
                    new AttrEnum("sans", "1"),
                    new AttrEnum("serif", "2"),
                    new AttrEnum("monospace", "3")
            ),
            new Attr("textStyle", "",
                    new AttrEnum("normal", "0"),
                    new AttrEnum("bold", "1"),
                    new AttrEnum("italic", "2")
            ),
     */

    private final static String[] KEYS = {
            "android:textStyle",
            "android:typeface",
            "android:fontFamily",
    };

    @Override
    public String[] getKeys() {
        return KEYS;
    }

    @Override
    public boolean supports(HashMap<String, String> map) {
        for (String key : KEYS)
            if (map.containsKey(key))
                return true;

        return false;
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        return new String[0];
    }

    @Override
    public String[] parse(HashMap<String, String> map, String viewName) {

        String value;
        String style = null, font = null;

        if (map.containsKey(KEYS[0])) {
            value = map.get(KEYS[0]);
            value = value.trim().replace(" ", "").toLowerCase();
            switch (value) {
                case "bold":
                    style = "android.graphics.Typeface.BOLD";
                    break;
                case "italic":
                    style = "android.graphics.Typeface.ITALIC";
                    break;
                case "normal":
                    style = "android.graphics.Typeface.NORMAL";
                    break;
                case "bold|italic":
                case "italic|bold":
                    style = "android.graphics.Typeface.BOLD_ITALIC";
                    break;
            }
        }

        if (map.containsKey(KEYS[2])) {
            font = AttrValueParser.parseFont(map.get(KEYS[2]));

        } else if (map.containsKey(KEYS[1])) {
            value = map.get(KEYS[1]);
            value = value.trim().replace(" ", "").toLowerCase();
            switch (value) {
                case "normal":
                    font = "android.graphics.Typeface.DEFAULT";
                    break;
                case "sans":
                    font = "android.graphics.Typeface.SANS_SERIF";
                    break;
                case "serif":
                    font = "android.graphics.Typeface.SERIF";
                    break;
                case "monospace":
                    font = "android.graphics.Typeface.MONOSPACE";
                    break;
            }
        }

        if (font == null)
            font = "android.graphics.Typeface.DEFAULT";

        for (String k : KEYS)
            map.remove(k);

        if (style != null && font.equals("android.graphics.Typeface.DEFAULT")
                && style.equals("android.graphics.Typeface.BOLD")) {
            font = "android.graphics.Typeface.DEFAULT_BOLD";
            style = null;
        }

        if (style != null) {
            return new String[]{"setTypeface(" + font + ", " + style + ")"};
        } else {
            return new String[]{"setTypeface(" + font + ")"};
        }

    }

}
