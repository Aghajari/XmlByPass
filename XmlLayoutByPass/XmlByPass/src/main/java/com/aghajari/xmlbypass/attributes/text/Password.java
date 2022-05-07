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
 * <code>android:password</code> parser
 */
public class Password implements AttributesParser {
    private final static String KEY = "android:password";

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
        String value = AttrValueParser.parseBoolean(map.get(KEY));
        map.remove(KEY);

        try {
            if ("true".equalsIgnoreCase(value))
                return new String[] {"setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)"};
        }catch (Exception ignore){}
        return new String[0];
    }


}
