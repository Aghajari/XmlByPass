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

import com.aghajari.xmlbypass.SourceGenerator;
import com.aghajari.xmlbypass.attributes.AttrValueParser;
import com.aghajari.xmlbypass.attributes.AttributesParser;

import java.util.HashMap;

public class OnClick implements AttributesParser {
    private final static String ANDROID_ON_CLICK = "android:onClick";

    @Override
    public String[] getKeys() {
        return new String[]{ANDROID_ON_CLICK};
    }

    @Override
    public boolean supports(HashMap<String, String> map) {
        return map.containsKey(ANDROID_ON_CLICK);
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        return null;
    }

    @Override
    public String[] parse(HashMap<String, String> map, String viewName, SourceGenerator generator) {
        String value = AttrValueParser.parseString(map.get(ANDROID_ON_CLICK));
        map.remove(ANDROID_ON_CLICK);

        String onClickClass = generator.getOnClickListenerClassName();

        return new String[]{
                "setOnClickListener(new " + onClickClass + "(" + value + "))"
        };
    }

    @Override
    public String[] imports() {
        return new String[]{
                "android.content.ContextWrapper",
                "android.view.View",
                "android.content.Context",
                "androidx.lifecycle.LifecycleOwner"
        };
    }
}
