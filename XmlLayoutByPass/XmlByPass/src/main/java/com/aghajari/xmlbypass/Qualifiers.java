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

package com.aghajari.xmlbypass;

import java.util.HashMap;

/**
 * <a href="https://developer.android.com/guide/topics/resources/providing-resources">
 * Stores all qualifiers and condition of their implementations
 */
class Qualifiers {

    private static final HashMap<String, String> qualifiers = new HashMap<>();

    static {
        qualifiers.put("land", "getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE");
        qualifiers.put("port", "getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT");

        qualifiers.put("small", "(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL");
        qualifiers.put("normal", "(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL");
        qualifiers.put("large", "(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE");
        qualifiers.put("xlarge", "(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE");

        qualifiers.put("notlong", "(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_LONG_MASK) == Configuration.SCREENLAYOUT_LONG_NO");
        qualifiers.put("long", "(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_LONG_MASK) == Configuration.SCREENLAYOUT_LONG_YES");

        qualifiers.put("notround", "!getResources().getConfiguration().isScreenRound()");
        qualifiers.put("round", "getResources().getConfiguration().isScreenRound()");

        qualifiers.put("ldltr", "(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_LAYOUTDIR_MASK) == Configuration.SCREENLAYOUT_LAYOUTDIR_LTR");
        qualifiers.put("ldrtl", "(getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_LAYOUTDIR_MASK) == Configuration.SCREENLAYOUT_LAYOUTDIR_RTL");

        qualifiers.put("desk", "(getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_DESK");
        qualifiers.put("car", "(getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_CAR");
        qualifiers.put("television", "(getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION");
        qualifiers.put("appliance", "(getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_APPLIANCE");
        qualifiers.put("watch", "(getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_WATCH");
        qualifiers.put("vrheadset", "(getResources().getConfiguration().uiMode & Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_VR_HEADSET");

        qualifiers.put("notnight", "!getResources().getConfiguration().isNightModeActive()");
        qualifiers.put("night", "getResources().getConfiguration().isNightModeActive()");

        qualifiers.put("nowidecg", "!getResources().getConfiguration().isScreenWideColorGamut()");
        qualifiers.put("widecg", "getResources().getConfiguration().isScreenWideColorGamut()");

        qualifiers.put("lowdr", "!getResources().getConfiguration().isScreenHdr()");
        qualifiers.put("highdr", "getResources().getConfiguration().isScreenHdr()");

        qualifiers.put("wheel", "getResources().getConfiguration().navigation == Configuration.NAVIGATION_WHEEL");
        qualifiers.put("trackball", "getResources().getConfiguration().navigation == Configuration.NAVIGATION_TRACKBALL");
        qualifiers.put("dpad", "getResources().getConfiguration().navigation == Configuration.NAVIGATION_DPAD");
        qualifiers.put("nonav", "getResources().getConfiguration().navigation == Configuration.NAVIGATION_NONAV");

        qualifiers.put("12key", "getResources().getConfiguration().keyboard == Configuration.KEYBOARD_12KEY");
        qualifiers.put("qwerty", "getResources().getConfiguration().keyboard == Configuration.KEYBOARD_QWERTY");
        qualifiers.put("nokeys", "getResources().getConfiguration().keyboard == Configuration.KEYBOARD_NOKEYS");

        qualifiers.put("keyshidden", "getResources().getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_YES");
        qualifiers.put("keysexposed", "getResources().getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_NO");

        qualifiers.put("navhidden", "getResources().getConfiguration().navigationHidden == Configuration.NAVIGATIONHIDDEN_YES");
        qualifiers.put("navexposed", "getResources().getConfiguration().navigationHidden == Configuration.NAVIGATIONHIDDEN_NO");

        qualifiers.put("finger", "getResources().getConfiguration().touchscreen == Configuration.TOUCHSCREEN_FINGER");
        qualifiers.put("notouch", "getResources().getConfiguration().touchscreen == Configuration.TOUCHSCREEN_NOTOUCH");

        qualifiers.put("mcc", "getResources().getConfiguration().mcc != 0");
        qualifiers.put("mnc", "getResources().getConfiguration().mnc != 0");
    }

    public static String parseCondition(String value) {
        if (qualifiers.containsKey(value))
            return qualifiers.get(value);

        String out;
        if (value.startsWith("v") &&
                (out = parseWithInt("android.os.Build.VERSION.SDK_INT >= ",
                        value.substring(1))) != null)
            return out;

        if (value.startsWith("mcc") &&
                (out = parseWithInt("getResources().getConfiguration().mcc == ",
                        value.substring(3))) != null)
            return out;

        if (value.startsWith("mnc") &&
                (out = parseWithInt("getResources().getConfiguration().mnc == ",
                        value.substring(3), "Configuration.MNC_ZERO")) != null)
            return out;

        if (value.startsWith("sw") && value.endsWith("dp") &&
                (out = parseWithInt("getResources().getConfiguration().smallestScreenWidthDp >= ",
                        value.substring(2, value.length() - 2))) != null)
            return out;

        if (value.startsWith("w") && value.endsWith("dp") &&
                (out = parseWithInt("getResources().getConfiguration().screenWidthDp >= ",
                        value.substring(1, value.length() - 2))) != null)
            return out;

        if (value.startsWith("h") && value.endsWith("dp") &&
                (out = parseWithInt("getResources().getConfiguration().screenHeightDp >= ",
                        value.substring(1, value.length() - 2))) != null)
            return out;

        switch (value) {
            case "nodpi":
                return "true";
            case "ldpi":
                return "getResources().getDisplayMetrics().densityDpi == 120";
            case "mdpi":
                return "getResources().getDisplayMetrics().densityDpi == 160";
            case "hdpi":
                return "getResources().getDisplayMetrics().densityDpi == 240";
            case "xhdpi":
                return "getResources().getDisplayMetrics().densityDpi == 320";
            case "xxhdpi":
                return "getResources().getDisplayMetrics().densityDpi == 480";
            case "xxxhdpi":
                return "getResources().getDisplayMetrics().densityDpi == 640";
            case "tvhdpi":
                return "getResources().getDisplayMetrics().densityDpi == 213";
            case "anydpi":
                return "getResources().getConfiguration().densityDpi == 0xfffe";
            case "nnndpi":
                return "getResources().getConfiguration().densityDpi == 0xffff";
        }

        if (value.length() == 2)
            return "getResources().getConfiguration().getLocales().get(0).getLanguage().equals(\"" + value.toLowerCase() + "\")";

        return "true";
    }

    private static String parseWithInt(String cond, String v) {
        try {
            int v2 = Integer.parseInt(v);
            return cond + v2;
        } catch (Exception ignore) {
        }
        return null;
    }

    private static String parseWithInt(String cond, String v, String zero) {
        try {
            int v2 = Integer.parseInt(v);
            return cond + (v2 != 0 ? v2 : zero);
        } catch (Exception ignore) {
        }
        return null;
    }
}
