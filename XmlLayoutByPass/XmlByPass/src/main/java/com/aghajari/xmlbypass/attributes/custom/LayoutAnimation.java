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
 * <code>android:layoutAnimation</code> parser
 */
public class LayoutAnimation implements AttributesParser {
    private final static String KEY = "android:layoutAnimation";

    @Override
    public String[] getKeys() {
        return new String[]{KEY};
    }

    @Override
    public boolean supports(HashMap<String, String> map) {
        return map.containsKey(KEY);
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        String value = AttrValueParser.parseAnimId(map.get(KEY));

        map.remove(KEY);
        return new String[]{"setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), " + value + "))"};
    }

    @Override
    public String[] imports() {
        return new String[]{"android.view.animation.AnimationUtils"};
    }
}
