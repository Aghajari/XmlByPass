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

package com.aghajari.xmlbypass.attributes.layoutparams;

import com.aghajari.xmlbypass.attributes.AttrValueParser;

import java.util.HashMap;
import java.util.List;

/**
 * <code>LinearLayout.LayoutParams</code> parser
 */
public class LinearLayout extends FrameLayout {

    public LinearLayout(String id, String className) {
        super(id, className);
    }

    @Override
    public List<String> parseToList(HashMap<String, String> map) {
        List<String> list = super.parseToList(map);
        if (map.containsKey("android:layout_weight")) {
            list.add(mId + ".weight = (float) " +
                    AttrValueParser.parseFloat(map.get("android:layout_weight")));
            map.remove("android:layout_weight");
        }
        return list;
    }

    @Override
    public String getType(String className) {
        return "android.widget.LinearLayout.LayoutParams";
    }

}
