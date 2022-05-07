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

package com.aghajari.xmlbypass.attributes.custom;

import com.aghajari.xmlbypass.attributes.AttrValueParser;
import com.aghajari.xmlbypass.attributes.AttributesParser;

import java.util.HashMap;

/**
 * Parser of ContentPadding attributes for CardView
 * <code>app:contentPadding</code>,
 * <code>app:contentPaddingLeft</code>,
 * <code>v:contentPaddingTop</code>,
 * <code>app:contentPaddingRight</code>,
 * <code>app:contentPaddingBottom</code>,
 */
public class ContentPadding implements AttributesParser {

    private final static String[] KEYS = {
            "app:contentPadding",
            "app:contentPaddingLeft",
            "app:contentPaddingTop",
            "app:contentPaddingRight",
            "app:contentPaddingBottom",
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
        String left, top, right, bottom;
        left = top = right = bottom = null;

        if (map.containsKey(KEYS[0]))
            left = top = right = bottom = AttrValueParser.parseDimensionInteger(map.get(KEYS[0]));
        if (map.containsKey(KEYS[1]))
            left = AttrValueParser.parseDimensionInteger(map.get(KEYS[1]));
        if (map.containsKey(KEYS[2]))
            top = AttrValueParser.parseDimensionInteger(map.get(KEYS[2]));
        if (map.containsKey(KEYS[3]))
            right = AttrValueParser.parseDimensionInteger(map.get(KEYS[3]));
        if (map.containsKey(KEYS[4]))
            bottom = AttrValueParser.parseDimensionInteger(map.get(KEYS[4]));

        if (left == null) left = "0";
        if (top == null) top = "0";
        if (right == null) right = "0";
        if (bottom == null) bottom = "0";

        for (String k : KEYS)
            map.remove(k);

        return new String[]{"setContentPadding(" + left + ", " + top + ", " + right + ", " + bottom + ")"};
    }

}
