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
 * <code>ViewGroup.LayoutParams</code> parser
 */
public class ViewGroup implements LayoutParamsAttributesParser {

    protected final String className;
    protected final String mId;

    public ViewGroup(String id, String className) {
        this.className = getType(className);
        mId = id + "_lp";
    }

    @Override
    public String[] getKeys() {
        return new String[0];
    }

    @Override
    public boolean supports(HashMap<String, String> map) {
        return true;
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        return parseToList(map).toArray(new String[0]);
    }

    public List<String> parseToList(HashMap<String, String> map) {
        List<String> list = new ArrayList<>();

        if (!map.containsKey("android:layout_width")) {
            list.add(className + " " + mId + " = new " + className + "(" +
                    mId.substring(0, mId.length() - 3) + ".getLayoutParams()" + ")");
        } else {
            String[] wh = parseWidthAndHeight(map);
            list.add(className + " " + mId + " = new " + className + "(" + wh[0] + ", " + wh[1] + ")");
        }
        return list;
    }

    @Override
    public String getVarName() {
        return mId;
    }

    @Override
    public String getType(String className) {
        if (className.equalsIgnoreCase("merge")) {
            return "ViewGroup.LayoutParams";
        }
        return className + ".LayoutParams";
    }

    public static String[] parseWidthAndHeight(HashMap<String, String> map) {
        String w = null, h = null;

        if (map.containsKey("android:layout_width")) {
            w = map.get("android:layout_width");
            if (w.equalsIgnoreCase("fill_parent")
                    || w.equalsIgnoreCase("match_parent"))
                w = "-1";
            else if (w.equalsIgnoreCase("wrap_content"))
                w = "-2";
            else
                w = AttrValueParser.parseDimensionInteger(w);

            map.remove("android:layout_width");
        }

        if (map.containsKey("android:layout_height")) {
            h = map.get("android:layout_height");
            if (h.equalsIgnoreCase("fill_parent")
                    || h.equalsIgnoreCase("match_parent"))
                h = "-1";
            else if (h.equalsIgnoreCase("wrap_content"))
                h = "-2";
            else
                h = AttrValueParser.parseDimensionInteger(h);

            map.remove("android:layout_height");
        }

        if (w == null)
            w = "-2";
        if (h == null)
            h = "-2";

        return new String[]{w, h};
    }

}
