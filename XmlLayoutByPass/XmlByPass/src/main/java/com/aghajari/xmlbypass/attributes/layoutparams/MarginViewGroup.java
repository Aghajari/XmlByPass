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
 * <code>ViewGroup.MarginLayoutParams</code> parser
 */
public class MarginViewGroup extends ViewGroup {

    public MarginViewGroup(String id, String className) {
        super(id, className);
    }

    @Override
    public List<String> parseToList(HashMap<String, String> map) {
        List<String> list = super.parseToList(map);
        parseMargin(list, map, mId);
        return list;
    }

    public static void parseMargin(List<String> list, HashMap<String, String> map, String mId) {
        String left, top, right, bottom, start, end;
        left = top = right = bottom = start = end = null;

        if (map.containsKey("android:layout_margin"))
            left = top = right = bottom = AttrValueParser.parseDimensionInteger(map.get("android:layout_margin"));
        if (map.containsKey("android:layout_marginLeft"))
            left = AttrValueParser.parseDimensionInteger(map.get("android:layout_marginLeft"));
        if (map.containsKey("android:layout_marginTop"))
            top = AttrValueParser.parseDimensionInteger(map.get("android:layout_marginTop"));
        if (map.containsKey("android:layout_marginRight"))
            right = AttrValueParser.parseDimensionInteger(map.get("android:layout_marginRight"));
        if (map.containsKey("android:layout_marginBottom"))
            bottom = AttrValueParser.parseDimensionInteger(map.get("android:layout_marginBottom"));
        if (map.containsKey("android:layout_marginStart"))
            start = AttrValueParser.parseDimensionInteger(map.get("android:layout_marginStart"));
        if (map.containsKey("android:layout_marginEnd"))
            end = AttrValueParser.parseDimensionInteger(map.get("android:layout_marginEnd"));
        if (map.containsKey("android:layout_marginVertical"))
            top = bottom = AttrValueParser.parseDimensionInteger(map.get("android:layout_marginVertical"));

        if (map.containsKey("android:layout_marginHorizontal")) {
            left = right = AttrValueParser.parseDimensionInteger(map.get("android:layout_marginHorizontal"));
            start = end = null;
        }

        if (left == null) left = "0";
        if (top == null) top = "0";
        if (right == null) right = "0";
        if (bottom == null) bottom = "0";
        if (start == null) start = "0";
        if (end == null) end = "0";

        map.remove("android:layout_margin");
        map.remove("android:layout_marginLeft");
        map.remove("android:layout_marginTop");
        map.remove("android:layout_marginRight");
        map.remove("android:layout_marginBottom");
        map.remove("android:layout_marginStart");
        map.remove("android:layout_marginEnd");
        map.remove("android:layout_marginVertical");
        map.remove("android:layout_marginHorizontal");


        int nonZeroCount = 0;
        nonZeroCount += !left.equals("0") ? 1 : 0;
        nonZeroCount += !top.equals("0") ? 1 : 0;
        nonZeroCount += !right.equals("0") ? 1 : 0;
        nonZeroCount += !bottom.equals("0") ? 1 : 0;

        if (nonZeroCount == 4)
            list.add(mId + ".setMargins(" + left + ", " + top + ", " + right + ", " + bottom + ")");
        else {
            if (!left.equals("0"))
                list.add(mId + ".leftMargin = " + left);
            if (!start.equals("0"))
                list.add(mId + ".setMarginStart(" + start + ")");
            if (!top.equals("0"))
                list.add(mId + ".topMargin = " + top);
            if (!right.equals("0"))
                list.add(mId + ".rightMargin = " + right);
            if (!end.equals("0"))
                list.add(mId + ".setMarginEnd(" + end + ")");
            if (!bottom.equals("0"))
                list.add(mId + ".bottomMargin = " + bottom);
        }
    }
}
