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
 * Parser of ChildIndicatorBounds attributes
 * <code>android:childIndicatorLeft</code>,
 * <code>android:childIndicatorRight</code>,
 * <code>android:childIndicatorStart</code>,
 * <code>android:childIndicatorEnd</code>
 */
public class ChildIndicatorBounds implements AttributesParser {

    private final static String[] KEYS = {
            "android:childIndicatorLeft",
            "android:childIndicatorRight",
            "android:childIndicatorStart",
            "android:childIndicatorEnd"
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
        String left, right, start, end;
        left = right = start = end = null;

        if (map.containsKey(KEYS[0]))
            left = AttrValueParser.parseDimensionInteger(map.get(KEYS[0]));
        if (map.containsKey(KEYS[1]))
            right = AttrValueParser.parseDimensionInteger(map.get(KEYS[1]));
        if (map.containsKey(KEYS[2]))
            start = AttrValueParser.parseDimensionInteger(map.get(KEYS[2]));
        if (map.containsKey(KEYS[3]))
            end = AttrValueParser.parseDimensionInteger(map.get(KEYS[3]));


        boolean relative = start != null || end != null;

        if (left == null) left = "0";
        if (right == null) right = "0";
        if (start == null) start = "0";
        if (end == null) end = "0";

        for (String k : KEYS)
            map.remove(k);

        if (relative) {
            return new String[]{"setChildIndicatorBoundsRelative(" + start + ", " + end + ")"};
        } else {
            return new String[]{"setChildIndicatorBounds(" + left + ", " + right + ")"};
        }
    }

}
