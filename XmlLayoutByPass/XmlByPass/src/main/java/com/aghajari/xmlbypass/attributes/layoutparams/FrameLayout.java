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

import com.aghajari.xmlbypass.attributes.Attr;
import com.aghajari.xmlbypass.attributes.AttrEnum;

import java.util.HashMap;
import java.util.List;

/**
 * <code>FrameLayout.LayoutParams</code> parser
 */
public class FrameLayout extends MarginViewGroup {

    public FrameLayout(String id, String className) {
        super(id, className);
    }

    @Override
    public List<String> parseToList(HashMap<String, String> map) {
        List<String> list = super.parseToList(map);
        String out = parseGravity(map);
        if (out != null)
            list.add(mId + ".gravity = " + out);

        return list;
    }

    public static String parseGravity(HashMap<String, String> map) {
        String out = null;

        if (map.containsKey("android:layout_gravity")) {
            Attr attr = new Attr("layout_gravity", "",
                    new AttrEnum("top", "android.view.Gravity.TOP"),
                    new AttrEnum("bottom", "android.view.Gravity.BOTTOM"),
                    new AttrEnum("left", "android.view.Gravity.LEFT"),
                    new AttrEnum("right", "android.view.Gravity.RIGHT"),
                    new AttrEnum("center_vertical", "android.view.Gravity.CENTER_VERTICAL"),
                    new AttrEnum("fill_vertical", "android.view.Gravity.FILL_VERTICAL"),
                    new AttrEnum("center_horizontal", "android.view.Gravity.CENTER_HORIZONTAL"),
                    new AttrEnum("fill_horizontal", "android.view.Gravity.FILL_HORIZONTAL"),
                    new AttrEnum("center", "android.view.Gravity.CENTER"),
                    new AttrEnum("fill", "android.view.Gravity.FILL"),
                    new AttrEnum("clip_vertical", "android.view.Gravity.CLIP_VERTICAL"),
                    new AttrEnum("clip_horizontal", "android.view.Gravity.CLIP_HORIZONTAL")
            );

            String[] gravity = attr.parse(map, true);
            if (gravity != null && gravity.length == 1) {
                out = gravity[0];
            }
            map.remove("android:layout_gravity");
        }
        return out;
    }

    @Override
    public String getType(String className) {
        return "android.widget.FrameLayout.LayoutParams";
    }
}
