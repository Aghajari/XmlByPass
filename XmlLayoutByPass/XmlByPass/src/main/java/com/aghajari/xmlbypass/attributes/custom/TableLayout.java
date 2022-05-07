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
 * Parser of TableLayout attributes
 * <code>android:stretchColumns</code>,
 * <code>android:shrinkColumns</code>,
 * <code>android:collapseColumns</code>
 */
public class TableLayout implements AttributesParser {

    private final static String[] KEYS = {
            "android:stretchColumns",
            "android:shrinkColumns",
            "android:collapseColumns",
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
        String stretchColumns, shrinkColumns, collapseColumns;
        stretchColumns = shrinkColumns = collapseColumns = null;

        if (map.containsKey(KEYS[0]))
            stretchColumns = AttrValueParser.parseString(map.get(KEYS[0]));
        if (map.containsKey(KEYS[1]))
            shrinkColumns = AttrValueParser.parseString(map.get(KEYS[1]));
        if (map.containsKey(KEYS[2]))
            collapseColumns = AttrValueParser.parseString(map.get(KEYS[2]));

        for (String key : KEYS)
            map.remove(key);

        ArrayList<String> out = new ArrayList<>();
        if (stretchColumns != null && !stretchColumns.equalsIgnoreCase("null")) {
            out.add("ArrayList<Integer> _tableStretchColumns = parseColumnsOfTableLayout(" + stretchColumns + ", true);");
            out.add("if (_tableStretchColumns == null)");
            out.add("\t" + mId + ".setStretchAllColumns(true);");
            out.add("else {");
            out.add("\tfor (Integer _tableStretchColumn: _tableStretchColumns)");
            out.add("\t\t" + mId + ".setColumnStretchable(_tableStretchColumn, true);");
            out.add("}");
        }
        if (shrinkColumns != null && !shrinkColumns.equalsIgnoreCase("null")) {
            out.add("ArrayList<Integer> _tableShrinkColumns = parseColumnsOfTableLayout(" + shrinkColumns + ", true);");
            out.add("if (_tableShrinkColumns == null)");
            out.add("\t" + mId + ".setShrinkAllColumns(true);");
            out.add("else {");
            out.add("\tfor (Integer _tableShrinkColumn: _tableShrinkColumns)");
            out.add("\t\t" + mId + ".setColumnShrinkable(_tableShrinkColumn, true);");
            out.add("}");
        }
        if (collapseColumns != null && !collapseColumns.equalsIgnoreCase("null")) {
            out.add("ArrayList<Integer> _tableCollapseColumns = parseColumnsOfTableLayout(" + shrinkColumns + ", false);");
            out.add("for (Integer _tableCollapseColumn: _tableCollapseColumns)");
            out.add("\t" + mId + ".setColumnCollapsed(_tableCollapseColumn, true);");
        }
        return out.toArray(new String[0]);
    }

    @Override
    public boolean needsToAddIdAtFirst(SourceGenerator generator) {
        generator.addExtraCode("    private static ArrayList<Integer> parseColumnsOfTableLayout(String sequence, boolean supportsAll) {\n" +
                "        if (supportsAll && sequence != null && sequence.charAt(0) == '*')\n" +
                "            return null;\n" +
                "        \n" +
                "        String[] columnDef = Pattern.compile(\"\\\\s*,\\\\s*\").split(sequence);\n" +
                "        ArrayList<Integer> columns = new ArrayList<>(columnDef.length);\n" +
                "        for (String columnIdentifier : columnDef) {\n" +
                "            try {\n" +
                "                int columnIndex = Integer.parseInt(columnIdentifier);\n" +
                "                if (columnIndex >= 0)\n" +
                "                    columns.add(columnIndex);\n" +
                "            } catch (NumberFormatException ignore) {\n" +
                "            }\n" +
                "        }\n" +
                "        return columns;\n" +
                "    }");
        return false;
    }

    @Override
    public boolean needsSemicolon() {
        return false;
    }


    /*
    private static ArrayList<Integer> parseColumns(String sequence, boolean supportsAll) {
        if (supportsAll && sequence != null && sequence.charAt(0) == '*')
            return null;

        String[] columnDef = Pattern.compile("\\s*,\\s*").split(sequence);
        ArrayList<Integer> columns = new ArrayList<>(columnDef.length);
        for (String columnIdentifier : columnDef) {
            try {
                int columnIndex = Integer.parseInt(columnIdentifier);
                if (columnIndex >= 0)
                    columns.add(columnIndex);
            } catch (NumberFormatException ignore) {
            }
        }
        return columns;
    }
     */

    @Override
    public String[] imports() {
        return new String[]{"java.util.ArrayList", "java.util.regex.Pattern"};
    }

}
