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

package com.aghajari.xmlbypass.attributes;

import java.util.HashMap;

/**
 * TintColors parser
 */
public class AttrTint extends Attr {

    public AttrTint(String name) {
        super(name, "color");
    }

    public AttrTint(String name, String codeName) {
        super(name, "color", codeName);
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        String value = map.get(getName());
        map.remove(getName());

        String func = getFunctionName() + "List";

        return new String[]{func + "(android.content.res.ColorStateList.valueOf(" +
                AttrValueParser.parseColor(value) + "))"};
    }
}
