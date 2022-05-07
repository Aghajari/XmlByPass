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

import com.aghajari.xmlbypass.SourceGenerator;
import com.aghajari.xmlbypass.attributes.AttrValueParser;
import com.aghajari.xmlbypass.attributes.AttributesParser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parser of RecyclerView attributes
 * <code>app:layoutManager</code>,
 * <code>app:spanCount</code>,
 * <code>app:reverseLayout</code>,
 * <code>app:stackFromEnd</code>
 * <code>android:orientation</code>
 */
public class RecyclerView implements AttributesParser {

    private final static String[] KEYS = {
            "app:layoutManager",
            "app:spanCount",
            "app:reverseLayout",
            "app:stackFromEnd"
    };

    @Override
    public String[] getKeys() {
        return KEYS;
    }

    @Override
    public boolean supports(HashMap<String, String> map) {
        return map.containsKey(KEYS[1]);
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        return null;
    }

    @Override
    public String[] parse(HashMap<String, String> map, String mId) {
        ArrayList<String> out = new ArrayList<>();
        String layoutManager = map.get(KEYS[0]);


        String o;
        if (map.containsKey("android:orientation") &&
                map.get("android:orientation").equalsIgnoreCase("horizontal"))
            o = "0";
        else
            o = "1";
        map.remove("android:orientation");

        String span = "1";
        String reverseLayout = "false";
        if (map.containsKey(KEYS[1]))
            span = AttrValueParser.parseInteger(map.get(KEYS[1]));
        if (map.containsKey(KEYS[2]))
            reverseLayout = AttrValueParser.parseBoolean(map.get(KEYS[2]));

        String stackFromEnd = "false";
        if (map.containsKey(KEYS[3]))
            stackFromEnd = AttrValueParser.parseBoolean(map.get(KEYS[3]));

        if (layoutManager.equalsIgnoreCase("androidx.recyclerview.widget.StaggeredGridLayoutManager")) {
            if (reverseLayout.equalsIgnoreCase("true")) {
                out.add(layoutManager + " " + mId + "_lm = new " +
                        layoutManager + "(" + span + ", " + o + ");");
                out.add(mId + "_lm.setReverseLayout(true);");
                out.add(mId + ".setLayoutManager(" + mId +"_lm);");
            } else {
                out.add(mId + ".setLayoutManager(new " + layoutManager + "(" + span + ", " + o + "));");
            }
        } else {
            if (stackFromEnd.equalsIgnoreCase("true") &&
                    layoutManager.equalsIgnoreCase("androidx.recyclerview.widget.LinearLayoutManager")) {
                out.add(layoutManager + " " + mId + "_lm = new " +
                        layoutManager + "(getContext(), " + o + ", " + reverseLayout + ");");
                out.add(mId + "_lm.setStackFromEnd(true);");
                out.add(mId + ".setLayoutManager(" + mId +"_lm);");
            } else {
                switch (layoutManager) {
                    case "androidx.recyclerview.widget.LinearLayoutManager":
                        out.add(mId + ".setLayoutManager(new " +
                                layoutManager + "(getContext(), " + o + ", " + reverseLayout + "));");
                        break;
                    case "androidx.recyclerview.widget.GridLayoutManager":
                        out.add(mId + ".setLayoutManager(new " +
                                layoutManager + "(getContext(), " + span + ", " + o + ", " + reverseLayout + "));");
                        break;
                }
            }
        }

        for (String key : KEYS)
            map.remove(key);

        return out.toArray(new String[0]);
    }

    @Override
    public boolean needsToAddIdAtFirst(SourceGenerator generator) {
        return false;
    }

    @Override
    public boolean needsSemicolon() {
        return false;
    }

}
