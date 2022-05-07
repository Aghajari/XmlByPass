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

import com.aghajari.xmlbypass.attributes.AttributesParser;

import java.util.HashMap;

/**
 * <code>android:layerType</code> parser
 */
public class LayerType implements AttributesParser {
    private final static String KEY = "android:layerType";

    @Override
    public String[] getKeys() {
        return new String[] {KEY};
    }

    @Override
    public boolean supports(HashMap<String, String> map) {
        return map.containsKey(KEY);
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        String value = map.get(KEY);
        map.remove(KEY);

        switch (value.toLowerCase()) {
            case "none":
                return new String[]{"setLayerType(0, null)"};
            case "software":
                return new String[]{"setLayerType(1, null)"};
            case "hardware":
                return new String[]{"setLayerType(2, null)"};
        }
        return null;
    }
}
