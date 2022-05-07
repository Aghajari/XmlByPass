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
 * <code>RelativeLayout.LayoutParams</code> parser
 */
public class RelativeLayout extends LinearLayout {

    public RelativeLayout(String id, String className) {
        super(id, className);
    }

    public static RelativeLayoutVerb[] verbs = new RelativeLayoutVerb[]{
            new RelativeLayoutVerb("layout_toLeftOf", "LEFT_OF"),
            new RelativeLayoutVerb("layout_toRightOf", "RIGHT_OF"),
            new RelativeLayoutVerb("layout_above", "ABOVE"),
            new RelativeLayoutVerb("layout_below", "BELOW"),
            new RelativeLayoutVerb("layout_alignBaseline", "ALIGN_BASELINE"),
            new RelativeLayoutVerb("layout_alignLeft", "ALIGN_LEFT"),
            new RelativeLayoutVerb("layout_alignTop", "ALIGN_TOP"),
            new RelativeLayoutVerb("layout_alignRight", "ALIGN_RIGHT"),
            new RelativeLayoutVerb("layout_alignBottom", "ALIGN_BOTTOM"),
            new RelativeLayoutVerb("layout_alignParentLeft", "ALIGN_PARENT_LEFT", true),
            new RelativeLayoutVerb("layout_alignParentTop", "ALIGN_PARENT_TOP", true),
            new RelativeLayoutVerb("layout_alignParentRight", "ALIGN_PARENT_RIGHT", true),
            new RelativeLayoutVerb("layout_alignParentBottom", "ALIGN_PARENT_BOTTOM", true),
            new RelativeLayoutVerb("layout_centerInParent", "CENTER_IN_PARENT", true),
            new RelativeLayoutVerb("layout_centerHorizontal", "CENTER_HORIZONTAL", true),
            new RelativeLayoutVerb("layout_centerVertical", "CENTER_VERTICAL", true),
            new RelativeLayoutVerb("layout_toStartOf", "START_OF"),
            new RelativeLayoutVerb("layout_toEndOf", "END_OF"),
            new RelativeLayoutVerb("layout_alignStart", "ALIGN_START"),
            new RelativeLayoutVerb("layout_alignEnd", "ALIGN_END"),
            new RelativeLayoutVerb("layout_alignParentStart", "ALIGN_PARENT_START", true),
            new RelativeLayoutVerb("layout_alignParentEnd", "ALIGN_PARENT_END", true),
    };
    private boolean importNeeded;

    @Override
    public List<String> parseToList(HashMap<String, String> map) {
        importNeeded = false;
        List<String> list = super.parseToList(map);

        for (RelativeLayoutVerb verb : verbs)
            importNeeded = importNeeded | verb.check(mId, list, map);

        if (map.containsKey("android:layout_alignWithParentIfMissing")) {
            list.add(mId + ".alignWithParent = " +
                    AttrValueParser.parseBoolean(map.get("android:layout_alignWithParentIfMissing")));
            map.remove("android:layout_alignWithParentIfMissing");
        }
        return list;
    }


    @Override
    public String[] imports() {
        if (importNeeded)
            return new String[]{"android.widget.RelativeLayout"};

        return null;
    }

    @Override
    public String getType(String className) {
        return "android.widget.RelativeLayout.LayoutParams";
    }

    public static class RelativeLayoutVerb {

        public final String key;
        private final String verb;
        private final boolean isBoolean;

        private RelativeLayoutVerb(String key, String verb) {
            this(key, verb, false);
        }

        private RelativeLayoutVerb(String key, String verb, boolean isBoolean) {
            this.key = "android:" + key;
            this.verb = "RelativeLayout." + verb;
            this.isBoolean = isBoolean;
        }

        public boolean check(String mId, List<String> list, HashMap<String, String> map) {
            if (map.containsKey(key)) {
                if (isBoolean) {
                    try {
                        if ("true".equalsIgnoreCase(map.get(key)))
                            list.add(mId + ".addRule(" + verb + ")");
                    } catch (Exception ignore) {
                    }
                } else {
                    list.add(mId + ".addRule(" + verb + ", " +
                            AttrValueParser.parseRefId(map.get(key)) + ")");
                }
                map.remove(key);
                return !isBoolean;
            }
            return false;
        }
    }
}
