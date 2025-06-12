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

import com.aghajari.xmlbypass.SourceGenerator;
import com.aghajari.xmlbypass.attributes.AttrValueParser;
import com.aghajari.xmlbypass.attributes.AttributesParser;

import java.util.HashMap;

/**
 * Parser of
 * <code>android:shadowColor</code>,
 * <code>android:shadowDx</code>,
 * <code>android:shadowDy</code>,
 * <code>android:shadowRadius</code>
 */
public class Shadow implements AttributesParser {

    /*
            new Attr("shadowColor", "color"),
            new Attr("shadowDx", "float"),
            new Attr("shadowDy", "float"),
            new Attr("shadowRadius", "float"),
     */

    private final static String[] KEYS = {
            "android:shadowColor",
            "android:shadowDx",
            "android:shadowDy",
            "android:shadowRadius",
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
    public String[] parse(HashMap<String, String> map, String viewName, SourceGenerator ignoredGenerator) {
        if (map.containsKey(KEYS[0])) {
            String color = AttrValueParser.parseColor(map.get(KEYS[0]));
            String dx = "0.0f";
            String dy = "0.0f";
            String rad = "0.0f";

            if (map.containsKey(KEYS[1]))
                dx = AttrValueParser.parseFloat(map.get(KEYS[1]));
            if (map.containsKey(KEYS[2]))
                dy = AttrValueParser.parseFloat(map.get(KEYS[2]));
            if (map.containsKey(KEYS[3]))
                rad = AttrValueParser.parseFloat(map.get(KEYS[3]));

            for (String k : KEYS)
                map.remove(k);

            return new String[]{"setShadowLayer(" + rad + ", " + dx + ", " + dy + ", " + color + ")"};
        } else {
            for (String k : KEYS)
                map.remove(k);
            return new String[0];
        }
    }

}
