package com.aghajari.xmlbypass.attributes.text;

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

import com.aghajari.xmlbypass.attributes.AttrValueParser;
import com.aghajari.xmlbypass.attributes.AttributesParser;

import java.util.HashMap;

/**
 * Parser of
 * <code>android:lineSpacingExtra</code>,
 * <code>android:lineSpacingMultiplier</code>
 */
public class LineSpacing implements AttributesParser {

    private final static String[] KEYS = {
            "android:lineSpacingExtra",
            "android:lineSpacingMultiplier"
    };

    @Override
    public String[] getKeys() {
        return KEYS;
    }

    @Override
    public boolean supports(HashMap<String, String> map) {
        for (String key : KEYS)
            if (map.containsKey(key))
                return true;

        return false;
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        String extra = null, mul = null;

        if (map.containsKey(KEYS[0]))
            extra = AttrValueParser.parseDimensionFloat(map.get(KEYS[0]));

        if (map.containsKey(KEYS[1]))
            mul = AttrValueParser.parseFloat(map.get(KEYS[1]));

        if (extra == null) extra = "0.0f";
        if (mul == null) mul = "1.0f";

        for (String k : KEYS)
            map.remove(k);

        return new String[]{"setLineSpacing(" + extra + ", " + mul + ")"};
    }

}
