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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <code>AbsoluteLayout.LayoutParams</code> parser
 */
public class AbsoluteLayout extends ViewGroup {

    public AbsoluteLayout(String id, String className) {
        super(id, className);
    }

    public List<String> parseToList(HashMap<String, String> map) {
        List<String> list = new ArrayList<>();

        String x = "0", y = "0";
        String[] wh = ViewGroup.parseWidthAndHeight(map);

        if (map.containsKey("android:layout_x")) {
            x = AttrValueParser.parseDimensionInteger(map.get("android:layout_x"));
            map.remove("android:layout_x");
        }

        if (map.containsKey("android:layout_y")) {
            y = AttrValueParser.parseDimensionInteger(map.get("android:layout_y"));
            map.remove("android:layout_y");
        }

        list.add(className + " " + mId + " = new " + className + "(" + wh[0] + ", " + wh[1] + ", " + x + ", " + y + ")");

        return list;
    }

    @Override
    public String getType(String className) {
        return "android.widget.AbsoluteLayout.LayoutParams";
    }

}
