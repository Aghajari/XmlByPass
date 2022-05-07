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
 * <code>android:divider</code> parser
 */
class AttrDivider extends Attr {

    public AttrDivider() {
        super("divider", "drawable");
    }

    // Same attr name, different method name :/
    @Override
    public boolean supports(HashMap<String, String> map, String name) {
        if (name.equals("LinearLayout"))
            codeName = "dividerDrawable";
        else
            codeName = "divider";
        return super.supports(map, name);
    }
}
