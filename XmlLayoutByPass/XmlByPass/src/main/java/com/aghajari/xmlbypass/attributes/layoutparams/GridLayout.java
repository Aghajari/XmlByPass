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
 * <code>GridLayout.LayoutParams</code> parser
 */
public class GridLayout extends ViewGroup {

    public GridLayout(String id, String className) {
        super(id, className);
    }

    @Override
    public List<String> parseToList(HashMap<String, String> map) {
        List<String> list = new ArrayList<>();
        list.add(className + " " + mId + " = new " + className + "()");
        String[] wh = parseWidthAndHeight(map);
        list.add(mId + ".width = " + wh[0]);
        list.add(mId + ".height = " + wh[1]);
        MarginViewGroup.parseMargin(list, map, mId);


        // col
        if (map.containsKey("android:layout_column")
                || map.containsKey("android:layout_columnSpan")
                || map.containsKey("android:layout_columnWeight")) {
            String column = null, span = null, weight = null;

            if (map.containsKey("android:layout_column"))
                column = AttrValueParser.parseInteger(map.get("android:layout_column"));
            if (map.containsKey("android:layout_columnSpan"))
                span = AttrValueParser.parseInteger(map.get("android:layout_columnSpan"));
            if (map.containsKey("android:layout_columnWeight"))
                weight = AttrValueParser.parseFloat(map.get("android:layout_columnWeight"));

            if (column == null)
                column = "Integer.MIN_VALUE";
            if (span == null)
                span = "1";
            if (weight == null)
                weight = "0.0f";

            list.add(mId + ".columnSpec = GridLayout.spec(" + column + ", " + span + ", " + weight + ")");
            map.remove("android:layout_column");
            map.remove("android:layout_columnSpan");
            map.remove("android:layout_columnWeight");
        }

        //row
        if (map.containsKey("android:layout_row")
                || map.containsKey("android:layout_rowSpan")
                || map.containsKey("android:layout_rowWeight")) {
            String row = null, span = null, weight = null;

            if (map.containsKey("android:layout_row"))
                row = AttrValueParser.parseInteger(map.get("android:layout_row"));
            if (map.containsKey("android:layout_rowSpan"))
                span = AttrValueParser.parseInteger(map.get("android:layout_rowSpan"));
            if (map.containsKey("android:layout_rowWeight"))
                weight = AttrValueParser.parseFloat(map.get("android:layout_rowWeight"));

            if (row == null)
                row = "Integer.MIN_VALUE";
            if (span == null)
                span = "1";
            if (weight == null)
                weight = "0.0f";

            list.add(mId + ".rowSpec = GridLayout.spec(" + row + ", " + span + ", " + weight + ")");
            map.remove("android:layout_row");
            map.remove("android:layout_rowSpan");
            map.remove("android:layout_rowWeight");
        }

        // gravity
        String gravity = FrameLayout.parseGravity(map);
        if (gravity != null)
            list.add(mId + ".setGravity(" + gravity + ")");
        return list;
    }


    @Override
    public String getType(String className) {
        return "android.widget.GridLayout.LayoutParams";
    }


    @Override
    public String[] imports() {
        return new String[]{"android.widget.GridLayout"};
    }
}
