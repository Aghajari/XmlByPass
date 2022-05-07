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
 * <code>android:drawableTop</code>,
 * <code>android:drawableBottom</code>,
 * <code>android:drawableLeft</code>,
 * <code>android:drawableRight</code>,
 * <code>android:drawableStart</code>,
 * <code>android:drawableEnd</code>
 */
public class CompoundDrawable implements AttributesParser {

    private final static String[] KEYS = {
            "android:drawableTop",
            "android:drawableBottom",
            "android:drawableLeft",
            "android:drawableRight",
            "android:drawableStart",
            "android:drawableEnd",
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

        if (map.containsKey("android:drawableLeft"))
            left = AttrValueParser.parseDrawable(map.get("android:drawableLeft"));
        if (map.containsKey("android:drawableTop"))
            top = AttrValueParser.parseDrawable(map.get("android:drawableTop"));
        if (map.containsKey("android:drawableRight"))
            right = AttrValueParser.parseDrawable(map.get("android:drawableRight"));
        if (map.containsKey("android:drawableBottom"))
            bottom = AttrValueParser.parseDrawable(map.get("android:drawableBottom"));
        if (map.containsKey("android:drawableStart"))
            start = AttrValueParser.parseDrawable(map.get("android:drawableStart"));
        if (map.containsKey("android:drawableEnd"))
            end = AttrValueParser.parseDrawable(map.get("android:drawableEnd"));


        boolean relative = start != null || end != null;

        if (left == null) left = "null";
        if (top == null) top = "null";
        if (right == null) right = "null";
        if (bottom == null) bottom = "null";
        if (start == null) start = "null";
        if (end == null) end = "null";

        for (String k : KEYS)
            map.remove(k);

        if (relative) {
            return new String[]{"setCompoundDrawablesRelativeWithIntrinsicBounds(" + start + ", " + top + ", " + end + ", " + bottom + ")"};
        } else {
            return new String[]{"setCompoundDrawablesWithIntrinsicBounds(" + left + ", " + top + ", " + right + ", " + bottom + ")"};
        }
    }

}
