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
 * <code>android:autoSizeTextType</code>,
 * <code>android:autoSizeStepGranularity</code>,
 * <code>android:autoSizeMinTextSize</code>,
 * <code>android:autoSizeMaxTextSize</code>
 */
public class AutoSize implements AttributesParser {

    private final static String[] KEYS = {
            "android:autoSizeTextType",
            "android:autoSizeStepGranularity",
            "android:autoSizeMinTextSize",
            "android:autoSizeMaxTextSize",
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
        boolean onlyType = false;

        String value;
        String min = null, max = null, step = null, type = null;

        if (map.containsKey(KEYS[0])) {
            value = map.get(KEYS[0]);
            if (value.trim().toLowerCase().equalsIgnoreCase("uniform"))
                type = "1";

            onlyType = true;

            for (int i = 1; i < KEYS.length - 1; i++) {
                if (map.containsKey(KEYS[i])) {
                    onlyType = false;
                    break;
                }
            }
        }


        if (map.containsKey(KEYS[1]))
            step = AttrValueParser.parseDimensionInteger(map.get(KEYS[1]));
        if (map.containsKey(KEYS[2]))
            min = AttrValueParser.parseDimensionInteger(map.get(KEYS[2]));
        if (map.containsKey(KEYS[3]))
            max = AttrValueParser.parseDimensionInteger(map.get(KEYS[3]));

        if (type == null) type = "0";
        if (min == null) min = viewName + ".getAutoSizeMinTextSize()";
        if (max == null) max = viewName + ".getAutoSizeMaxTextSize()";
        if (step == null) step = viewName + ".getAutoSizeStepGranularity()";

        for (String k : KEYS)
            map.remove(k);

        if (onlyType) {
            return new String[]{"setAutoSizeTextTypeWithDefaults(" + type + ")"};
        } else {
            return new String[]{"setAutoSizeTextTypeUniformWithConfiguration(" +
                    min + ", " + max + ", " + step + ", " + type + ", 0" + ")"};
        }

    }

}
