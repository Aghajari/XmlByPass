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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Parser of background or whatever needs drawable
 */
public class AttrDrawable extends Attr {

    public final String drawableFunc, colorFunc, resFunc;

    public AttrDrawable(String name, @Nonnull String drawableFunc,
                        @Nullable String colorFunc,
                        @Nullable String resFunc) {
        super(name, "drawable");
        this.drawableFunc = drawableFunc;
        this.colorFunc = colorFunc;
        this.resFunc = resFunc;
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        String value = map.get(getName());
        map.remove(getName());

        if (value.equalsIgnoreCase("@null") || value.equalsIgnoreCase("null"))
            return new String[]{drawableFunc + "(null)"};

        if (colorFunc != null && !colorFunc.isEmpty()) {
            if (value.startsWith("@android:color/")) {
                value = value.substring("@android:color/".length());
                return new String[]{colorFunc + "(getResources().getColor(android.R.color." + AttrValueParser.getAnyResName(value) + "))"};
            } else if (value.startsWith("@color/")) {
                value = value.substring("@color/".length());
                return new String[]{colorFunc + "(getResources().getColor(R.color." + AttrValueParser.getAnyResName(value) + "))"};
            } else if (value.startsWith("#")) {
                value = value.substring(1).trim().toUpperCase();
                if (value.length() == 6)
                    value = "0xFF" + value;
                else
                    value = "0x" + value;
                return new String[]{colorFunc + "(" + value + ")"};
            }
        }

        if (resFunc != null && !resFunc.isEmpty()) {
            if (AttrValueParser.isAttrValue(value)) {
                return new String[]{resFunc + "(" + AttrValueParser.resolveAttribute(value) + ")"};
            } else if (value.startsWith("@android:drawable/")) {
                value = value.substring("@android:drawable/".length());
                return new String[]{resFunc + "(android.R.drawable." + AttrValueParser.getAnyResName(value) + ")"};
            } else if (value.startsWith("@drawable/")) {
                value = value.substring("@drawable/".length());
                return new String[]{resFunc + "(R.drawable." + AttrValueParser.getAnyResName(value) + ")"};
            }
        }

        return new String[]{drawableFunc + "(" + AttrValueParser.parseDrawable(value) + ")"};
    }
}
