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
import com.aghajari.xmlbypass.attributes.AttributesParser;

import java.util.HashMap;

/**
 * <code>android:theme</code> and <code>style</code> parser
 */
public class Theme implements AttributesParser {
    private final static String ANDROID_THEME = "android:theme";
    private final static String STYLE = "style";

    @Override
    public String[] getKeys() {
        return new String[]{ANDROID_THEME, STYLE};
    }

    @Override
    public boolean supports(HashMap<String, String> map) {
        return map.containsKey(ANDROID_THEME) || map.containsKey(STYLE);
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        String id = getThemStyleId(map);
        map.remove(ANDROID_THEME);
        map.remove(STYLE);

        return new String[]{"new ContextThemeWrapper(getContext(), " + id + ")"};
    }

    public String parse(String name, HashMap<String, String> map) {
        map.remove(ANDROID_THEME);
        map.remove(STYLE);

        return "new ContextThemeWrapper(getContext(), R.style." + name + ")";
    }

    @Override
    public String[] imports() {
        return new String[]{"androidx.appcompat.view.ContextThemeWrapper"};
    }

    public String getThemStyleId(HashMap<String, String> map) {
        if (map.containsKey(ANDROID_THEME))
            return AttrValueParser.parseRefStyleId(map.get(ANDROID_THEME));

        return AttrValueParser.parseRefStyleId(map.get(STYLE));
    }

    public String getStyleId(HashMap<String, String> map) {
        return AttrValueParser.parseRefStyleId(map.get(STYLE));
    }

    public String getThemStyleName(HashMap<String, String> map) {
        if (map.containsKey(ANDROID_THEME))
            return AttrValueParser.parseRefStyleId(map.get(ANDROID_THEME));

        return AttrValueParser.parseStyleName(map.get(STYLE));
    }

}
