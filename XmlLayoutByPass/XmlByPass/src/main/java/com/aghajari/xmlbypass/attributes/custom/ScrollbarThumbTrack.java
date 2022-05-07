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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Parser of Scrollbar attributes
 * <code>android:scrollbarThumbHorizontal</code>,
 * <code>android:scrollbarThumbVertical</code>,
 * <code>android:scrollbarTrackHorizontal</code>,
 * <code>android:scrollbarTrackVertical</code>
 */
public class ScrollbarThumbTrack implements AttributesParser {
    private final static String[] KEYS = {
            "android:scrollbarThumbHorizontal",
            "android:scrollbarThumbVertical",
            "android:scrollbarTrackHorizontal",
            "android:scrollbarTrackVertical"
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
        List<String> out = new ArrayList<>(KEYS.length);
        for (String key : KEYS) {
            if (map.containsKey(key)) {
                String func = key.substring("android:scrollbar".length());
                boolean track = func.toLowerCase().startsWith("track");
                func = func.substring(5);
                func = "set" + func + "Scrollbar" + (track ? "Track" : "Thumb") + "Drawable";
                out.add(func + "(" + AttrValueParser.parseDrawable(map.get(key)) + ")");
                map.remove(key);
            }
        }
        return out.toArray(new String[0]);
    }

}
