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

/**
 * Value reader & parser for all supported formats
 */
public class AttrValueParser {

    private static final String[] ATTR_STARTER = new String[] {
            "?android:attr/",
            "?attr/android:",
            "?attr/",
            "?android:",
            "?",
    };

    public static String getAnyResName(String name) {
        if (name.startsWith("attr/"))
            name = name.substring(5);

        return name.replace('.', '_');
    }

    private static String getAttrResName(String value) {
        for (String starter: ATTR_STARTER) {
            if (value.startsWith(starter))
                return value.substring(starter.length());
        }
        return value;
    }

    public static String resolveAttribute(String name) {
        boolean isAndroidR = name.contains("android:");

        String resId;
        if (isAndroidR)
            resId = "android.R.attr." + getAttrResName(name);
        else
            resId = "R.attr." + getAttrResName(name);

        return "resolveAttribute(" + resId + ")";
    }

    public static boolean isAttrValue(String value) {
        if (!value.startsWith("?"))
            return false;

        return getAttrResName(value).matches("^[a-zA-Z._][a-zA-Z0-9_.]*?$");
    }

    public static String parseBoolean(String value) {
        if (isAttrValue(value)) {
            return "getResources().getBoolean(" + resolveAttribute(value) + ")";
        }

        if (value.startsWith("@android:bool/")) {
            value = value.substring("@android:bool/".length());
            return "getResources().getBoolean(android.R.bool." + getAnyResName(value) + ")";
        } else if (value.startsWith("@bool/")) {
            value = value.substring("@bool/".length());
            return "getResources().getBoolean(R.bool." + getAnyResName(value) + ")";
        }

        return "true".equalsIgnoreCase(value) ? "true" : "false";
    }

    public static String parseDimensionInteger(String value) {
        if (isAttrValue(value)) {
            return "getResources().getDimensionPixelSize(" + resolveAttribute(value) + ")";
        }

        if (value.startsWith("@android:dimen/")) {
            value = value.substring("@android:dimen/".length());
            return "getResources().getDimensionPixelSize(android.R.dimen." + getAnyResName(value) + ")";
        } else if (value.startsWith("@dimen/")) {
            value = value.substring("@dimen/".length());
            return "getResources().getDimensionPixelSize(R.dimen." + getAnyResName(value) + ")";
        }

        return getDimensionWithFunction(value, true);
    }

    public static String parseDimensionFloat(String value) {
        if (isAttrValue(value)) {
            return "getResources().getDimension(" + resolveAttribute(value) + ")";
        }

        if (value.startsWith("@android:dimen/")) {
            value = value.substring("@android:dimen/".length());
            return "getResources().getDimension(android.R.dimen." + getAnyResName(value) + ")";
        } else if (value.startsWith("@dimen/")) {
            value = value.substring("@dimen/".length());
            return "getResources().getDimension(R.dimen." + getAnyResName(value) + ")";
        }

        return getDimensionWithFunction(value, false);
    }

    private static String getDimensionWithFunction(String value, boolean integer) {
        value = value.toLowerCase().trim();
        if (getUnit(value) != null) {
            String funcName = value.endsWith("dip") ? "dp" : value.substring(value.length() - 2);
            value = value.substring(0, value.endsWith("dip")
                    ? value.length() - 3 : value.length() - 2);

            if (value.endsWith("d"))
                value = value.substring(value.length() - 1);
            if (!value.endsWith("f"))
                value += "f";

            //return "android.util.TypedValue.applyDimension(android.util.TypedValue." + unit + ", (float) " + value + ", getResources().getDisplayMetrics())";
            return (integer && !funcName.equals("dp") ? "(int) " : "") + funcName + "(" + value + ")";
        }
        return value;
    }

    public static boolean isDimensionValue(String value) {
        if (getUnit(value) != null) {
            try {
                value = value.substring(0, value.length() - (value.endsWith("dip") ? 3 : 2));
                Float.parseFloat(value);
                return true;
            } catch (Exception ignore) {
            }
        }
        return false;
    }

    public static String getUnit(String value) {
        if (value.endsWith("dp") || value.endsWith("dip")) {
            return "COMPLEX_UNIT_DIP";
        } else if (value.endsWith("px")) {
            return "COMPLEX_UNIT_PX";
        } else if (value.endsWith("sp")) {
            return "COMPLEX_UNIT_SP";
        } else if (value.endsWith("pt")) {
            return "COMPLEX_UNIT_PT";
        } else if (value.endsWith("in")) {
            return "COMPLEX_UNIT_IN";
        } else if (value.endsWith("mm")) {
            return "COMPLEX_UNIT_MM";
        }
        return null;
    }

    public static String parseFloat(String value) {
        value = parseInteger(value);
        if (value.charAt(0) != 'g') {
            String value2 = value;
            if (value2.toLowerCase().endsWith("d"))
                value2 = value2.substring(value2.length() - 1);
            if (!value2.toLowerCase().endsWith("f"))
                value2 += "f";

            try {
                Float.parseFloat(value2);
                return value2;
            } catch (Exception ignore) {
            }
        }
        return "(float) " + value;
    }

    public static String parseInteger(String value) {
        if (isAttrValue(value)) {
            return "getResources().getInteger(" + resolveAttribute(value) + ")";
        }

        if (value.startsWith("@android:integer/")) {
            value = value.substring("@android:integer/".length());
            return "getResources().getInteger(android.R.integer." + getAnyResName(value) + ")";
        } else if (value.startsWith("@integer/")) {
            value = value.substring("@integer/".length());
            return "getResources().getInteger(R.integer." + getAnyResName(value) + ")";
        }
        return value;
    }

    public static String parseDrawable(String value) {
        if (value.equalsIgnoreCase("@null") || value.equalsIgnoreCase("null")
                || value.isEmpty())
            return "null";

        if (isAttrValue(value)) {
            return "getResources().getDrawable(" + resolveAttribute(value) + ")";
        }

        if (value.startsWith("@android:color/")) {
            value = value.substring("@android:color/".length());
            return "new android.graphics.drawable.ColorDrawable(getResources().getColor(android.R.color." + getAnyResName(value) + "))";
        } else if (value.startsWith("@color/")) {
            value = value.substring("@color/".length());
            return "new android.graphics.drawable.ColorDrawable(getResources().getColor(R.color." + getAnyResName(value) + "))";
        } else if (value.startsWith("#")) {
            value = value.substring(1).trim().toUpperCase();
            if (value.length() == 6)
                value = "0xFF" + value;
            else
                value = "0x" + value;
            return "new android.graphics.drawable.ColorDrawable(" + value + ")";
        } else if (value.startsWith("@android:drawable/")) {
            value = value.substring("@android:drawable/".length());
            return "getResources().getDrawable(android.R.drawable." + getAnyResName(value) + ", null)";
        } else if (value.startsWith("@drawable/")) {
            value = value.substring("@drawable/".length());
            return "getResources().getDrawable(R.drawable." + getAnyResName(value) + ", null)";
        }

        return value;
    }

    public static String parseRefId(String value) {
        if (value == null)
            return "0";

        String v2 = parseRefId(value, "id");
        if (v2.equals(value))
            return parseRefId(value, "+id");
        return v2;
    }

    public static String parseAnimId(String value) {
        return parseRefId(value, "anim");
    }

    public static String parseRefStyleId(String value) {
        return parseRefId(value, "style");
    }

    public static String parseStyleName(String value) {
        if (value.equalsIgnoreCase("@null") || value.equalsIgnoreCase("null"))
            return null;

        if (value.toLowerCase().startsWith("@android:style/")) {
            value = value.substring(("@android:style/").length());
            return getAnyResName(value);
        } else if (value.toLowerCase().startsWith("@style/")) {
            value = value.substring(("@style/").length());
            return getAnyResName(value);
        }

        if (value.startsWith("?"))
            return value.substring(1);

        return value;
    }

    public static String parseRefId(String value, String type) {
        if (value.equalsIgnoreCase("@null") || value.equalsIgnoreCase("null"))
            return "0";

        if (isAttrValue(value)) {
            return resolveAttribute(value);
        }

        if (!type.startsWith("+") && value.startsWith("@android:" + type + "/")) {
            value = value.substring(("@android:" + type + "/").length());
            return "android.R." + type + "." + getAnyResName(value);
        } else if (value.startsWith("@" + type + "/")) {
            value = value.substring(("@" + type + "/").length());
            if (type.startsWith("+"))
                type = type.substring(1);

            return "R." + type + "." + getAnyResName(value);
        }
        return value;
    }


    public static String parseRef(String value) {
        if (value.equalsIgnoreCase("@null") || value.equalsIgnoreCase("null"))
            return "0";

        if (isAttrValue(value))
            return resolveAttribute(value);

        String type;

        if (value.startsWith("@") && value.contains("/")) {
            if (value.startsWith("@android:")) {
                type = value.substring("@android:".length(), value.indexOf('/'));
                value = value.substring(("@android:" + type + "/").length());

                return "android.R." + type + "." + getAnyResName(value);
            } else {
                type = value.substring(1, value.indexOf('/'));
                value = value.substring(("@" + type + "/").length());
                if (type.startsWith("+"))
                    type = type.substring(1);

                return "R." + type + "." + getAnyResName(value);
            }
        }
        return value;
    }

    public static String parseColor(String value) {
        if (isAttrValue(value)) {
            return "getResources().getColor(" + resolveAttribute(value) + ")";
        }

        if (value.startsWith("@android:color/")) {
            value = value.substring("@android:color/".length());
            return "getResources().getColor(android.R.color." + getAnyResName(value) + ")";
        } else if (value.startsWith("@color/")) {
            value = value.substring("@color/".length());
            return "getResources().getColor(R.color." + getAnyResName(value) + ")";
        } else if (value.startsWith("#")) {
            value = value.substring(1).trim().toUpperCase();
            if (value.length() == 6)
                value = "0xFF" + value;
            else
                value = "0x" + value;
            return value;
        }
        return value;
    }

    public static String parseString(String value) {
        if (value == null)
            return "\"\"";

        if (isAttrValue(value)) {
            return "getResources().getString(" + resolveAttribute(value) + ")";
        }

        if (value.startsWith("@android:string/")) {
            value = value.substring("@android:string/".length());
            return "getResources().getString(android.R.string." + getAnyResName(value) + ")";
        } else if (value.startsWith("@string/")) {
            value = value.substring("@string/".length());
            return "getResources().getString(R.string." + getAnyResName(value) + ")";
        }

        if (value.startsWith("\\?") && isAttrValue(value.substring(1)))
            value = value.substring(1);

        return "\"" + value + "\"";
    }

    public static String parseFont(String value) {
        String resStr = null;
        if (isAttrValue(value)) {
            resStr = resolveAttribute(value);
        }

        if (resStr == null) {
            if (value.startsWith("@android:string/")) {
                value = value.substring("@android:string/".length());
                resStr = "getResources().getString(android.R.string." + getAnyResName(value) + ")";
            } else if (value.startsWith("@string/")) {
                value = value.substring("@string/".length());
                resStr = "getResources().getString(R.string." + getAnyResName(value) + ")";
            }

            if (resStr != null) {
                resStr = "getResources().getIdentifier(" + resStr + ", \"font\", getContext().getPackageName())";
            } else {
                if (value.startsWith("@android:font/")) {
                    value = value.substring("@android:font/".length());
                    resStr = "android.R.font." + getAnyResName(value);
                } else if (value.startsWith("@font/")) {
                    value = value.substring("@font/".length());
                    resStr = "R.font." + getAnyResName(value);
                } else {
                    resStr = "R.font." + getAnyResName(value);
                }
            }
        }

        return "androidx.core.content.res.ResourcesCompat.getFont(getContext(), " + resStr + ")";
    }
}
