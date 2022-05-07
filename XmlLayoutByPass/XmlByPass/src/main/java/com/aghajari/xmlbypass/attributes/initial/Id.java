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

package com.aghajari.xmlbypass.attributes.initial;

import com.aghajari.xmlbypass.attributes.AttributesParser;

import java.util.HashMap;
import java.util.HashSet;

/**
 * <code>android:id</code> parser
 */
public class Id implements AttributesParser {
    private final static String ANDROID_ID = "android:id";

    public final HashMap<String, String> ids = new HashMap<>();
    public final HashMap<String, String> ids_saved = new HashMap<>();

    @Override
    public String[] getKeys() {
        return new String[]{ANDROID_ID};
    }

    @Override
    public boolean supports(HashMap<String, String> map) {
        return map.containsKey(ANDROID_ID);
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        String mId = getOriginalViewId(map);

        map.remove(ANDROID_ID);
        return new String[]{"setId(R.id." + mId + ")"};
    }

    public String getOriginalViewId(HashMap<String, String> map) {
        return getOriginalViewId(map.get(ANDROID_ID));
    }

    public static String getOriginalViewId(String value) {
        if (value.startsWith("@+id/"))
            value = value.substring(5);
        if (value.startsWith("@id/"))
            value = value.substring(4);

        return value;
    }

    public String getViewId(HashMap<String, String> map, String cls) {
        return getUniqueViewId(cls, getOriginalViewId(map));
    }

    public String getUniqueViewId(String cls, String value) {
        String id = value;
        int index = 0;
        while (ids.containsKey(id)) {
            index++;
            id = value + index;
        }
        ids.put(id, cls);

        if (ids_saved.containsKey(id) && !ids_saved.get(id).equals(cls))
            throw new RuntimeException("The public views of the layouts with " +
                    "different qualifiers must be same type: " + ids_saved.get(id) + " or " + cls);
        return id;
    }
}
