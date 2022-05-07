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
 * <code>ConstraintLayout.LayoutParams</code> parser
 */
public class ConstraintLayout extends MarginViewGroup {

    public final static ConstraintLayoutVerb[] verbs = new ConstraintLayoutVerb[]{
            new ConstraintLayoutVerb("layout_constrainedHeight", "constrainedHeight", "boolean"),
            new ConstraintLayoutVerb("layout_constrainedWidth", "constrainedWidth", "boolean"),
            new ConstraintLayoutVerb("layout_constraintBaseline_toBaselineOf", "baselineToBaseline", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintBottom_toBottomOf", "bottomToBottom", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintBottom_toTopOf", "bottomToTop", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintCircleAngle", "circleAngle", "integer"),
            new ConstraintLayoutVerb("layout_constraintCircle", "circleConstraint", "reference"),
            new ConstraintLayoutVerb("layout_constraintCircleRadius", "circleRadius", "dimension"),
            new ConstraintLayoutVerb("layout_constraintTag", "constraintTag", "string"),
            new ConstraintLayoutVerb("layout_constraintDimensionRatio", "dimensionRatio", "string"),
            new ConstraintLayoutVerb("layout_editor_absoluteX", "editorAbsoluteX", "dimension"),
            new ConstraintLayoutVerb("layout_editor_absoluteY", "editorAbsoluteY", "dimension"),
            new ConstraintLayoutVerb("layout_constraintEnd_toEndOf", "endToEnd", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintEnd_toStartOf", "endToStart", "reference|enum"),
            new ConstraintLayoutVerb("layout_goneMarginBottom", "goneBottomMargin", "dimension"),
            new ConstraintLayoutVerb("layout_goneMarginEnd", "goneEndMargin", "dimension"),
            new ConstraintLayoutVerb("layout_goneMarginLeft", "goneLeftMargin", "dimension"),
            new ConstraintLayoutVerb("layout_goneMarginRight", "goneRightMargin", "dimension"),
            new ConstraintLayoutVerb("layout_goneMarginStart", "goneStartMargin", "dimension"),
            new ConstraintLayoutVerb("layout_goneMarginTop", "goneTopMargin", "dimension"),
            new ConstraintLayoutVerb("layout_constraintGuide_begin", "guideBegin", "dimension"),
            new ConstraintLayoutVerb("layout_constraintGuide_end", "guideEnd", "dimension"),
            new ConstraintLayoutVerb("layout_constraintGuide_percent", "guidePercent", "float"),
            new ConstraintLayoutVerb("layout_constraintHorizontal_bias", "horizontalBias", "float"),
            new ConstraintLayoutVerb("layout_constraintHorizontal_weight", "horizontalWeight", "float"),
            new ConstraintLayoutVerb("layout_constraintHorizontal_chainStyle", "horizontalChainStyle", "chain"),
            new ConstraintLayoutVerb("layout_constraintVertical_bias", "verticalBias", "float"),
            new ConstraintLayoutVerb("layout_constraintVertical_weight", "verticalWeight", "float"),
            new ConstraintLayoutVerb("layout_constraintVertical_chainStyle", "verticalChainStyle", "chain"),
            new ConstraintLayoutVerb("layout_constraintLeft_toLeftOf", "leftToLeft", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintLeft_toRightOf", "leftToRight", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintRight_toLeftOf", "rightToLeft", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintRight_toRightOf", "rightToRight", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintStart_toEndOf", "startToEnd", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintStart_toStartOf", "startToStart", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintTop_toBottomOf", "topToBottom", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintTop_toTopOf", "topToTop", "reference|enum"),
            new ConstraintLayoutVerb("layout_constraintHeight_default", "matchConstraintDefaultHeight", "def"),
            new ConstraintLayoutVerb("layout_constraintHeight_max", "matchConstraintMaxHeight", "wrap"),
            new ConstraintLayoutVerb("layout_constraintHeight_min", "matchConstraintMinHeight", "wrap"),
            new ConstraintLayoutVerb("layout_constraintHeight_percent", "matchConstraintPercentHeight", "float"),
            new ConstraintLayoutVerb("layout_constraintWidth_default", "matchConstraintDefaultWidth", "def"),
            new ConstraintLayoutVerb("layout_constraintWidth_max", "matchConstraintMaxWidth", "wrap"),
            new ConstraintLayoutVerb("layout_constraintWidth_min", "matchConstraintMinWidth", "wrap"),
            new ConstraintLayoutVerb("layout_constraintWidth_percent", "matchConstraintPercentWidth", "float"),
            new ConstraintLayoutVerb("android:orientation", "orientation", "orientation"),
    };

    public ConstraintLayout(String id, String className) {
        super(id, className);
    }

    @Override
    public List<String> parseToList(HashMap<String, String> map) {
        List<String> list = super.parseToList(map);

        for (ConstraintLayoutVerb verb : verbs)
            verb.check(mId, list, map);

        return list;
    }

    @Override
    public String getType(String className) {
        return "androidx.constraintlayout.widget.ConstraintLayout.LayoutParams";
    }

    public static class ConstraintLayoutVerb {

        public final String key;
        public final String verb;
        public final String format;

        private ConstraintLayoutVerb(String key, String verb, String format) {
            this.key = (key.startsWith("android:") ? "" : "app:") + key;
            this.verb = verb;
            this.format = format;
        }

        public void check(String mId, List<String> list, HashMap<String, String> map) {
            if (map.containsKey(key)) {
                String value = null;
                String data = map.get(key);

                switch (format) {
                    case "boolean":
                        value = AttrValueParser.parseBoolean(data);
                        break;
                    case "string":
                        value = AttrValueParser.parseString(data);
                        break;
                    case "float":
                        value = AttrValueParser.parseFloat(data);
                        break;
                    case "integer":
                        value = AttrValueParser.parseInteger(data);
                        break;
                    case "dimension":
                        value = AttrValueParser.parseDimensionInteger(data);
                        break;
                    case "wrap":
                        if (data.equalsIgnoreCase("wrap"))
                            value = "-2";
                        else
                            value = AttrValueParser.parseDimensionInteger(data);
                        break;
                    case "reference":
                        value = AttrValueParser.parseRefId(data);
                        break;
                    case "reference|enum":
                        if (data.equalsIgnoreCase("parent"))
                            value = "0";
                        else
                            value = AttrValueParser.parseRefId(data);
                        break;
                    case "orientation":
                        if (data.equalsIgnoreCase("horizontal"))
                            value = "0";
                        else
                            value = "1";
                        break;
                    case "def":
                        if (data.equalsIgnoreCase("spread"))
                            value = "0";
                        else if (data.equalsIgnoreCase("wrap"))
                            value = "1";
                        else // percent
                            value = "2";
                        break;
                    case "chain":
                        if (data.equalsIgnoreCase("spread"))
                            value = "0";
                        else if (data.equalsIgnoreCase("spread_inside"))
                            value = "1";
                        else // packed
                            value = "2";
                        break;
                }

                if (value != null) {
                    list.add(mId + "." + verb + " = " + value);
                    map.remove(key);
                }
            }
        }
    }
}
