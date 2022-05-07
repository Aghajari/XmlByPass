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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Attr implements AttributesParser {

    // name=value to view.setName(value)
    // codeName = setName

    public String name, codeName, format, targetClass;
    public final List<AttrEnum> enums;
    public boolean needsContext;

    public Attr(String name, String format, AttrEnum... enums) {
        this(name, format, "", enums);
    }

    public Attr(String name, String format, String codeName, AttrEnum... enums) {
        this(name, format, codeName, null, enums);
    }

    public Attr needContext() {
        needsContext = true;

        return this;
    }

    public Attr(String name, String format, String codeName, String target, AttrEnum... enums) {
        this.name = name;
        this.codeName = (codeName == null || codeName.isEmpty()) ? name : codeName;
        this.format = format;
        this.enums = Arrays.asList(enums);
        this.targetClass = target;

        if (this.codeName.contains(":"))
            this.codeName = this.codeName.substring(this.codeName.indexOf(':') + 1);
    }

    public String getName() {
        return name.contains(":") ? name : "android:" + name;
    }

    @Override
    public String[] getKeys() {
        return new String[]{getName()};
    }

    @Override
    public boolean supports(HashMap<String, String> map) {
        return map.containsKey(getName());
    }

    @Override
    public boolean supports(HashMap<String, String> map, String name) {
        Set<String> targets = new HashSet<>();
        if (targetClass != null) {
            if (targetClass.contains("|")) {
                targets.addAll(Arrays.asList(targetClass.split("\\|")));
            } else {
                targets.add(targetClass);
            }
        }
        return supports(map)
                && (targetClass == null || targets.isEmpty() || targets.contains(name));
    }

    @Override
    public String[] parse(HashMap<String, String> map) {
        return parse(map, false);
    }

    public String getFunctionName() {
        return "set" + codeName.substring(0, 1).toUpperCase() + codeName.substring(1);

    }

    public String[] parse(HashMap<String, String> map, boolean onlyEnum) {
        String key = getName();
        String value = map.get(key);
        map.remove(key);

        String func = getFunctionName();

        boolean valueParsed = false;
        if (enums != null && enums.size() > 0) {
            Set<String> set = new HashSet<>();
            if (value.contains("|")) {
                String[] vl = value.split("\\|");
                for (String v : vl)
                    set.add(v.toLowerCase().trim());
            } else
                set.add(value.toLowerCase().trim());

            StringBuilder outValue = new StringBuilder();
            for (AttrEnum en : enums) {
                if (set.contains(en.key.toLowerCase())) {
                    outValue.append(en.value).append("|");
                    valueParsed = true;
                }
            }
            if (valueParsed) {
                value = outValue.toString();
                value = value.substring(0, value.length() - 1);
            }
        }

        if (onlyEnum)
            return new String[]{value};

        if (!valueParsed) {
            String old = value;

            switch (format) {
                case "boolean":
                    value = AttrValueParser.parseBoolean(value);
                    break;
                case "float":
                case "integer":
                    value = AttrValueParser.parseInteger(value);
                    break;
                case "dimension|integer":
                case "dimension":
                    value = AttrValueParser.parseDimensionInteger(value);
                    break;
                case "dimension|float":
                    value = AttrValueParser.parseDimensionFloat(value);
                    break;
                case "reference":
                    value = AttrValueParser.parseRef(value);
                    break;
                case "reference_id":
                    value = AttrValueParser.parseRefId(value);
                    break;
                case "color":
                    value = AttrValueParser.parseColor(value);
                    break;
                case "string":
                    value = AttrValueParser.parseString(value);
                    break;
                case "drawable":
                    value = AttrValueParser.parseDrawable(value);
                    break;
                case "drawable_id":
                    value = AttrValueParser.parseRefId(value, "drawable");
                    break;
                case "style_id":
                    value = AttrValueParser.parseRefStyleId(value);
                    break;
                case "interpolator_id":
                    value = AttrValueParser.parseRefId(value, "interpolator");
                    break;
                case "anim_id":
                    value = AttrValueParser.parseAnimId(value);
                    break;
                case "string_id":
                    value = AttrValueParser.parseRefId(value, "string");
                    break;
            }

            if (old.equals(value) && format.endsWith("_id"))
                value = AttrValueParser.parseRefId(value, format.substring(0, format.length() - 3));

        }
        return new String[]{func + (needsContext ? "(getContext(), " : "(") + value + ")"};
    }
}
