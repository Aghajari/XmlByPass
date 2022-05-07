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
 * Parser of Padding attributes
 * <code>android:padding</code>,
 * <code>android:paddingHorizontal</code>,
 * <code>android:paddingVertical</code>,
 * <code>android:paddingLeft</code>,
 * <code>android:paddingTop</code>,
 * <code>android:paddingRight</code>,
 * <code>android:paddingBottom</code>,
 * <code>android:paddingStart</code>,
 * <code>android:paddingEnd</code>
 */
public class Padding implements AttributesParser {

    private final static String[] KEYS = {
            "android:padding",
            "android:paddingHorizontal",
            "android:paddingVertical",
            "android:paddingLeft",
            "android:paddingTop",
            "android:paddingRight",
            "android:paddingBottom",
            "android:paddingStart",
            "android:paddingEnd"
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
        String left, top, right, bottom, start, end;
        left = top = right = bottom = start = end = null;

        if (map.containsKey("android:padding"))
            left = top = right = bottom = AttrValueParser.parseDimensionInteger(map.get("android:padding"));
        if (map.containsKey("android:paddingLeft"))
            left = AttrValueParser.parseDimensionInteger(map.get("android:paddingLeft"));
        if (map.containsKey("android:paddingTop"))
            top = AttrValueParser.parseDimensionInteger(map.get("android:paddingTop"));
        if (map.containsKey("android:paddingRight"))
            right = AttrValueParser.parseDimensionInteger(map.get("android:paddingRight"));
        if (map.containsKey("android:paddingBottom"))
            bottom = AttrValueParser.parseDimensionInteger(map.get("android:paddingBottom"));
        if (map.containsKey("android:paddingStart"))
            start = AttrValueParser.parseDimensionInteger(map.get("android:paddingStart"));
        if (map.containsKey("android:paddingEnd"))
            end = AttrValueParser.parseDimensionInteger(map.get("android:paddingEnd"));
        if (map.containsKey("android:paddingVertical"))
            top = bottom = AttrValueParser.parseDimensionInteger(map.get("android:paddingVertical"));

        if (map.containsKey("android:paddingHorizontal")) {
            left = right = AttrValueParser.parseDimensionInteger(map.get("android:paddingHorizontal"));
            start = end = null;
        }

        boolean relative = start != null || end != null;

        if (left == null) left = "0";
        if (top == null) top = "0";
        if (right == null) right = "0";
        if (bottom == null) bottom = "0";
        if (start == null) start = "0";
        if (end == null) end = "0";

        for (String k : KEYS)
            map.remove(k);

        if (relative) {
            return new String[]{"setPaddingRelative(" + start + ", " + top + ", " + end + ", " + bottom + ")"};
        } else {
            return new String[]{"setPadding(" + left + ", " + top + ", " + right + ", " + bottom + ")"};
        }
    }

}
