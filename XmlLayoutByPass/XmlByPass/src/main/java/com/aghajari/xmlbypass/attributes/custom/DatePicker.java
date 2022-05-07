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
 * Parser of DatePicker attributes
 * <code>android:minDate</code>,
 * <code>android:maxDate</code>
 */
public class DatePicker implements AttributesParser {

    private final static String[] KEYS = {
            "android:minDate",
            "android:maxDate"
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
        return null;
    }

    @Override
    public String[] parse(HashMap<String, String> map, String mId) {
        String minDate = null, maxDate = null;

        if (map.containsKey(KEYS[0]))
            minDate = AttrValueParser.parseString(map.get(KEYS[0]));
        if (map.containsKey(KEYS[1]))
            maxDate = AttrValueParser.parseString(map.get(KEYS[1]));

        for (String key : KEYS)
            map.remove(key);

        ArrayList<String> out = new ArrayList<>();
        if (minDate != null && !minDate.equalsIgnoreCase("null")) {
            out.add("try {");
            out.add("\t" + mId + ".setMinDate(new SimpleDateFormat(\"MM/dd/yyyy\").parse(" + minDate + ").getTime());");
            out.add("} catch (Exception ignore) {");
            out.add("}");
        }
        if (maxDate != null && !maxDate.equalsIgnoreCase("null")) {
            out.add("try {");
            out.add("\t" + mId + ".setMaxDate(new SimpleDateFormat(\"MM/dd/yyyy\").parse(" + maxDate + ").getTime());");
            out.add("} catch (Exception ignore) {");
            out.add("}");
        }
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

    /*
		try {
			new SimpleDateFormat().parse("").getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
     */

    @Override
    public String[] imports() {
        return new String[]{"java.text.SimpleDateFormat"};
    }

}
