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

import com.aghajari.xmlbypass.XmlByPassAttr;
import com.aghajari.xmlbypass.attributes.custom.ChildIndicatorBounds;
import com.aghajari.xmlbypass.attributes.custom.ContentPadding;
import com.aghajari.xmlbypass.attributes.custom.DatePicker;
import com.aghajari.xmlbypass.attributes.custom.Entries;
import com.aghajari.xmlbypass.attributes.custom.FadingEdge;
import com.aghajari.xmlbypass.attributes.custom.IndicatorBounds;
import com.aghajari.xmlbypass.attributes.custom.LayerType;
import com.aghajari.xmlbypass.attributes.custom.LayoutAnimation;
import com.aghajari.xmlbypass.attributes.custom.Padding;
import com.aghajari.xmlbypass.attributes.custom.RecyclerView;
import com.aghajari.xmlbypass.attributes.custom.ScrollbarThumbTrack;
import com.aghajari.xmlbypass.attributes.custom.Scrollbars;
import com.aghajari.xmlbypass.attributes.custom.TableLayout;
import com.aghajari.xmlbypass.attributes.custom.Toolbar;
import com.aghajari.xmlbypass.attributes.initial.OnClick;
import com.aghajari.xmlbypass.attributes.layoutparams.AbsoluteLayout;
import com.aghajari.xmlbypass.attributes.layoutparams.ConstraintLayout;
import com.aghajari.xmlbypass.attributes.layoutparams.FrameLayout;
import com.aghajari.xmlbypass.attributes.layoutparams.GridLayout;
import com.aghajari.xmlbypass.attributes.layoutparams.LayoutParamsAttributesParser;
import com.aghajari.xmlbypass.attributes.layoutparams.MarginViewGroup;
import com.aghajari.xmlbypass.attributes.layoutparams.RelativeLayout;
import com.aghajari.xmlbypass.attributes.text.AutoSize;
import com.aghajari.xmlbypass.attributes.text.CompoundDrawable;
import com.aghajari.xmlbypass.attributes.text.CompoundDrawableTint;
import com.aghajari.xmlbypass.attributes.text.LineSpacing;
import com.aghajari.xmlbypass.attributes.text.MaxLength;
import com.aghajari.xmlbypass.attributes.text.Numeric;
import com.aghajari.xmlbypass.attributes.text.Password;
import com.aghajari.xmlbypass.attributes.text.Shadow;
import com.aghajari.xmlbypass.attributes.text.TextLocale;
import com.aghajari.xmlbypass.attributes.text.TextSize;
import com.aghajari.xmlbypass.attributes.text.Typeface;

import java.util.HashMap;

public class AttrFactory {

    public static LayoutParamsAttributesParser findBestLayoutParamsParser(String id, String className, HashMap<String, String> map) {
        if (className.equalsIgnoreCase("FrameLayout") ||
                className.equalsIgnoreCase("android.widget.FrameLayout"))
            return new FrameLayout(id, className);

        if (className.equalsIgnoreCase("RelativeLayout") ||
                className.equalsIgnoreCase("android.widget.RelativeLayout")
                || map.containsKey("android:layout_alignWithParentIfMissing"))
            return new RelativeLayout(id, className);

        if (className.equalsIgnoreCase("ConstraintLayout") ||
                className.equalsIgnoreCase("androidx.constraintlayout.widget.ConstraintLayout"))
            return new ConstraintLayout(id, className);

        if (className.equalsIgnoreCase("AbsoluteLayout") ||
                className.equalsIgnoreCase("android.widget.AbsoluteLayout"))
            return new AbsoluteLayout(id, className);

        // ignore android:orientation
        for (ConstraintLayout.ConstraintLayoutVerb verb : ConstraintLayout.verbs)
            if (verb.key.startsWith("app:") && map.containsKey(verb.key))
                return new ConstraintLayout(id, className);

        for (RelativeLayout.RelativeLayoutVerb verb : RelativeLayout.verbs)
            if (map.containsKey(verb.key))
                return new RelativeLayout(id, className);

        if (className.equalsIgnoreCase("LinearLayout") ||
                className.equalsIgnoreCase("android.widget.LinearLayout") ||
                map.containsKey("android:layout_weight"))
            return new RelativeLayout(id, "LinearLayout");

        if (className.equalsIgnoreCase("GridLayout")
                || className.equalsIgnoreCase("android.widget.GridLayout")
                || map.containsKey("android:layout_row")
                || map.containsKey("android:layout_rowSpan")
                || map.containsKey("android:layout_rowWeight")
                || map.containsKey("android:layout_column")
                || map.containsKey("android:layout_columnSpan")
                || map.containsKey("android:layout_columnWeight")) {
            return new GridLayout(id, className);
        }

        if (map.containsKey("android:layout_gravity"))
            return new FrameLayout(id, className);

        if (map.containsKey("android:layout_x") || map.containsKey("android:layout_y"))
            return new AbsoluteLayout(id, className);

        return new MarginViewGroup(id, className);
    }

    public static AttrEnum[] tintModes = new AttrEnum[]{
            new AttrEnum("src_over", "android.graphics.PorterDuff.Mode.SRC_OVER"),
            new AttrEnum("src_in", "android.graphics.PorterDuff.Mode.SRC_IN"),
            new AttrEnum("src_atop", "android.graphics.PorterDuff.Mode.SRC_ATOP"),
            new AttrEnum("multiply", "android.graphics.PorterDuff.Mode.MULTIPLY"),
            new AttrEnum("screen", "android.graphics.PorterDuff.Mode.SCREEN"),
            new AttrEnum("add", "android.graphics.PorterDuff.Mode.ADD")
    };

    public static AttributesParser[] customs = new AttributesParser[]{
            new OnClick(),
            /* CUSTOM */
            new FadingEdge(),
            new LayerType(),
            new Padding(),
            new Scrollbars(),
            new ScrollbarThumbTrack(),
            /* VIEW GROUP */
            new LayoutAnimation(),
            /* SPINNER */
            new Entries(),
            /* ExpandableListView */
            new IndicatorBounds(),
            new ChildIndicatorBounds(),
            /* TableLayout */
            new TableLayout(),
            /* DatePicker */
            new DatePicker(),
            /* Toolbar */
            new Toolbar(),
            /* RecyclerView */
            new RecyclerView(),
            /* CardView */
            new ContentPadding(),
            /* TEXT */
            new AutoSize(),
            new CompoundDrawable(),
            new CompoundDrawableTint(),
            new LineSpacing(),
            new MaxLength(),
            new Numeric(),
            new Password(),
            new TextLocale(),
            new TextSize(),
            new Typeface(),
            new Shadow()
    };

    public static Attr[] attrs = new Attr[]{
            new Attr("tag", "string"), /* Set initial.Tag for setTag(id, value) */
            new Attr("scrollX", "dimension|integer"),
            new Attr("scrollY", "dimension|integer"),
            new Attr("focusable", "boolean",
                    new AttrEnum("auto", "0x00000010")
            ),
            new Attr("importantForAutofill", "",
                    new AttrEnum("auto", "0"),
                    new AttrEnum("yes", "0x1"),
                    new AttrEnum("no", "0x2"),
                    new AttrEnum("yesExcludeDescendants", "0x4"),
                    new AttrEnum("noExcludeDescendants", "0x8")
            ),
            new Attr("importantForContentCapture", "",
                    new AttrEnum("auto", "0"),
                    new AttrEnum("yes", "0x1"),
                    new AttrEnum("no", "0x2"),
                    new AttrEnum("yesExcludeDescendants", "0x4"),
                    new AttrEnum("noExcludeDescendants", "0x8")
            ),
            new Attr("focusableInTouchMode", "boolean"),
            new Attr("visibility", "",
                    new AttrEnum("visible", "0"),
                    new AttrEnum("invisible", "1"),
                    new AttrEnum("gone", "2")
            ),
            new Attr("fitsSystemWindows", "boolean"),
            new Attr("scrollbarStyle", "",
                    new AttrEnum("insideOverlay", "0x0"),
                    new AttrEnum("insideInset", "0x01000000"),
                    new AttrEnum("outsideOverlay", "0x02000000"),
                    new AttrEnum("outsideInset", "0x03000000")
            ),
            new Attr("isScrollContainer", "boolean", "scrollContainer"),
            new Attr("fadeScrollbars", "boolean", "scrollbarFadingEnabled"),
            new Attr("scrollbarFadeDuration", "integer"),
            new Attr("scrollbarDefaultDelayBeforeFade", "integer"),
            new Attr("scrollbarSize", "dimension|integer"),
            new Attr("fadingEdgeLength", "dimension|integer"),
            new Attr("nextFocusLeft", "reference_id", "nextFocusLeftId"),
            new Attr("nextFocusRight", "reference_id", "nextFocusRightId"),
            new Attr("nextFocusUp", "reference_id", "nextFocusUpId"),
            new Attr("nextFocusDown", "reference_id", "nextFocusDownId"),
            new Attr("nextFocusForward", "reference_id", "nextFocusForwardId"),
            new Attr("clickable", "boolean"),
            new Attr("longClickable", "boolean"),
            new Attr("contextClickable", "boolean"),
            new Attr("saveEnabled", "boolean"),
            new Attr("filterTouchesWhenObscured", "boolean"),
            new Attr("drawingCacheQuality", "",
                    new AttrEnum("auto", "0"),
                    new AttrEnum("low", "1"),
                    new AttrEnum("high", "2")
            ),
            new Attr("keepScreenOn", "boolean"),
            new Attr("duplicateParentState", "boolean", "duplicateParentStateEnabled"),
            new AttrMin(true), new AttrMin(false),
            new Attr("soundEffectsEnabled", "boolean"),
            new Attr("hapticFeedbackEnabled", "boolean"),
            new Attr("contentDescription", "string"),
            new Attr("accessibilityTraversalBefore", "integer"),
            new Attr("accessibilityTraversalAfter", "integer"),
            new Attr("overScrollMode", "enum",
                    new AttrEnum("always", "0"),
                    new AttrEnum("ifContentScrolls", "1"),
                    new AttrEnum("never", "2")
            ),
            new Attr("alpha", "float"),
            new Attr("elevation", "dimension|float"),
            new Attr("translationX", "dimension|float"),
            new Attr("translationY", "dimension|float"),
            new Attr("translationZ", "dimension|float"),
            new Attr("transformPivotX", "dimension|float", "pivotX"),
            new Attr("transformPivotY", "dimension|float", "pivotY"),
            new Attr("rotation", "float"),
            new Attr("rotationX", "float"),
            new Attr("rotationY", "float"),
            new Attr("scaleX", "float"),
            new Attr("scaleY", "float"),
            new Attr("verticalScrollbarPosition", "",
                    new AttrEnum("defaultPosition", "0"),
                    new AttrEnum("left", "1"),
                    new AttrEnum("right", "2")
            ),
            new Attr("layoutDirection", "",
                    new AttrEnum("ltr", "0"),
                    new AttrEnum("rtl", "1"),
                    new AttrEnum("inherit", "2"),
                    new AttrEnum("locale", "3")
            ),
            new Attr("textDirection", "integer",
                    new AttrEnum("inherit", "0"),
                    new AttrEnum("firstStrong", "1"),
                    new AttrEnum("anyRtl", "2"),
                    new AttrEnum("ltr", "3"),
                    new AttrEnum("rtl", "4"),
                    new AttrEnum("locale", "5"),
                    new AttrEnum("firstStrongLtr", "6"),
                    new AttrEnum("firstStrongRtl", "7")
            ),
            new Attr("textAlignment", "integer",
                    new AttrEnum("inherit", "0"),
                    new AttrEnum("gravity", "1"),
                    new AttrEnum("textStart", "2"),
                    new AttrEnum("textEnd", "3"),
                    new AttrEnum("center", "4"),
                    new AttrEnum("viewStart", "5"),
                    new AttrEnum("viewEnd", "6")
            ),
            new Attr("importantForAccessibility", "integer",
                    new AttrEnum("auto", "0"),
                    new AttrEnum("yes", "1"),
                    new AttrEnum("no", "2"), new
                    AttrEnum("noHideDescendants", "4"))
            ,
            new Attr("accessibilityLiveRegion", "integer",
                    new AttrEnum("none", "0"),
                    new AttrEnum("polite", "1"),
                    new AttrEnum("assertive", "2")
            ),
            new Attr("labelFor", "reference_id"),
            new Attr("transitionName", "string"),
            new Attr("nestedScrollingEnabled", "boolean"),
            new AttrDrawable("background",
                    "setBackground",
                    "setBackgroundColor",
                    "setBackgroundResource"),
            new AttrTint("backgroundTint"),
            new Attr("backgroundTintMode", "", tintModes),
            new Attr("outlineProvider", "", "",
                    new AttrEnum("none", "null"),
                    new AttrEnum("background", "android.view.ViewOutlineProvider.BACKGROUND"),
                    new AttrEnum("bounds", "android.view.ViewOutlineProvider.BOUNDS"),
                    new AttrEnum("paddedBounds", "android.view.ViewOutlineProvider.PADDED_BOUNDS")
            ),
            new Attr("foregroundGravity", "",
                    new AttrEnum("top", "android.view.Gravity.TOP"),
                    new AttrEnum("bottom", "android.view.Gravity.BOTTOM"),
                    new AttrEnum("left", "android.view.Gravity.LEFT"),
                    new AttrEnum("right", "android.view.Gravity.RIGHT"),
                    new AttrEnum("center_vertical", "android.view.Gravity.CENTER_VERTICAL"),
                    new AttrEnum("fill_vertical", "android.view.Gravity.FILL_VERTICAL"),
                    new AttrEnum("center_horizontal", "android.view.Gravity.CENTER_HORIZONTAL"),
                    new AttrEnum("fill_horizontal", "android.view.Gravity.FILL_HORIZONTAL"),
                    new AttrEnum("center", "android.view.Gravity.CENTER"),
                    new AttrEnum("fill", "android.view.Gravity.FILL"),
                    new AttrEnum("clip_vertical", "android.view.Gravity.CLIP_VERTICAL"),
                    new AttrEnum("clip_horizontal", "android.view.Gravity.CLIP_HORIZONTAL")
            ),
            new Attr("gravity", "",
                    new AttrEnum("top", "android.view.Gravity.TOP"),
                    new AttrEnum("bottom", "android.view.Gravity.BOTTOM"),
                    new AttrEnum("left", "android.view.Gravity.LEFT"),
                    new AttrEnum("right", "android.view.Gravity.RIGHT"),
                    new AttrEnum("center_vertical", "android.view.Gravity.CENTER_VERTICAL"),
                    new AttrEnum("fill_vertical", "android.view.Gravity.FILL_VERTICAL"),
                    new AttrEnum("center_horizontal", "android.view.Gravity.CENTER_HORIZONTAL"),
                    new AttrEnum("fill_horizontal", "android.view.Gravity.FILL_HORIZONTAL"),
                    new AttrEnum("center", "android.view.Gravity.CENTER"),
                    new AttrEnum("fill", "android.view.Gravity.FILL"),
                    new AttrEnum("clip_vertical", "android.view.Gravity.CLIP_VERTICAL"),
                    new AttrEnum("clip_horizontal", "android.view.Gravity.CLIP_HORIZONTAL")
            ),
            new Attr("foreground", "drawable"),
            new AttrTint("foregroundTint"),
            new Attr("foregroundTintMode", "", tintModes),
            new Attr("scrollIndicators", "",
                    new AttrEnum("none", "0x00"),
                    new AttrEnum("top", "0x01"),
                    new AttrEnum("bottom", "0x02"),
                    new AttrEnum("left", "0x04"),
                    new AttrEnum("right", "0x08"),
                    new AttrEnum("start", "0x10"),
                    new AttrEnum("end", "0x20")
            ),
            new Attr("tooltipText", "string"),
            new Attr("keyboardNavigationCluster", "boolean"),
            new Attr("nextClusterForward", "reference_id"),
            new Attr("focusedByDefault", "boolean"),
            new Attr("defaultFocusHighlightEnabled", "boolean"),
            new Attr("screenReaderFocusable", "boolean"),
            new Attr("accessibilityPaneTitle", "string"),
            new Attr("accessibilityHeading", "boolean"),
            new Attr("outlineSpotShadowColor", "color"),
            new Attr("outlineAmbientShadowColor", "color"),
            new Attr("forceDarkAllowed", "boolean"),
            /* Common */
            new AttrDivider(),
            new Attr("enabled", "boolean"),
            new Attr("height", "dimension|integer"),
            new Attr("width", "dimension|integer"),
            new Attr("maxHeight", "dimension|integer"),
            new Attr("maxWidth", "dimension|integer"),
            new Attr("x", "dimension|float"),
            new Attr("y", "dimension|float"),
            new Attr("orientation", "", "",
                    "LinearLayout|GridLayout|RadioGroup|SlidingDrawer",
                    new AttrEnum("horizontal", "0"),
                    new AttrEnum("vertical", "1")
            ),
            /* ViewGroup */
            new Attr("clipChildren", "boolean"),
            new Attr("clipToPadding", "boolean"),
            new Attr("animationCache", "boolean", "animationCacheEnabled"),
            new Attr("persistentDrawingCache", "integer",
                    new AttrEnum("none", "0x0"),
                    new AttrEnum("animation", "0x1"),
                    new AttrEnum("scrolling", "0x2"),
                    new AttrEnum("all", "0x3")
            ),
            new Attr("alwaysDrawnWithCache", "boolean", "alwaysDrawnWithCacheEnabled"),
            new Attr("descendantFocusability", "integer",
                    new AttrEnum("beforeDescendants", "0"),
                    new AttrEnum("afterDescendants", "1"),
                    new AttrEnum("blocksDescendants", "2")
            ),
            new Attr("touchscreenBlocksFocus", "boolean"),
            new Attr("splitMotionEvents", "boolean", "motionEventSplittingEnabled"),
            new Attr("layoutMode", "integer",
                    new AttrEnum("clipBounds", "0"),
                    new AttrEnum("opticalBounds", "1")
            ),
            new Attr("transitionGroup", "boolean"),
            /* AbsListView */
            new Attr("listSelector", "drawable", "selector"),
            new Attr("drawSelectorOnTop", "boolean"),
            new Attr("stackFromBottom", "boolean"),
            new Attr("scrollingCache", "boolean", "scrollingCacheEnabled"),
            new Attr("textFilterEnabled", "boolean"),
            new Attr("transcriptMode", "integer",
                    new AttrEnum("disabled", "0"),
                    new AttrEnum("normal", "1"),
                    new AttrEnum("alwaysScroll", "2")
            ),
            new Attr("cacheColorHint", "color"),
            new Attr("fastScrollEnabled", "boolean"),
            new Attr("fastScrollStyle", "style_id"),
            new Attr("smoothScrollbar", "boolean", "smoothScrollbarEnabled"),
            new Attr("choiceMode", "integer",
                    new AttrEnum("none", "0"),
                    new AttrEnum("singleChoice", "1"),
                    new AttrEnum("multipleChoice", "2"),
                    new AttrEnum("multipleChoiceModal", "3")
            ),
            new Attr("fastScrollAlwaysVisible", "boolean"),
            /* Chronometer */
            new Attr("format", "string"),
            new Attr("countDown", "boolean"),
            /* CompoundButton */
            new Attr("checked", "boolean"),
            new Attr("selected", "boolean"),
            new AttrDrawable("button",
                    "setButtonDrawable",
                    null,
                    "setButtonDrawable"
            ),
            new AttrTint("buttonTint"),
            new Attr("buttonTintMode", "", tintModes),
            /* CheckedTextView */
            new AttrDrawable("checkMark",
                    "setCheckMarkDrawable",
                    null,
                    "setCheckMarkDrawable"
            ),
            new AttrTint("checkMarkTint"),
            new Attr("checkMarkTintMode", "", tintModes),
            /* FrameLayout */
            new Attr("measureAllChildren", "boolean"),
            /* ExpandableListView */
            new Attr("groupIndicator", "drawable"),
            new Attr("childIndicator", "drawable"),
            new Attr("childDivider", "drawable"),
            /* Gallery */
            new Attr("animationDuration", "integer"),
            new Attr("spacing", "dimension"),
            new Attr("unselectedAlpha", "float"),
            /* GridView */
            new Attr("horizontalSpacing", "dimension"),
            new Attr("verticalSpacing", "dimension"),
            new Attr("stretchMode", "",
                    new AttrEnum("none", "0"),
                    new AttrEnum("spacingWidth", "1"),
                    new AttrEnum("columnWidth", "2"),
                    new AttrEnum("spacingWidthUniform", "3")
            ),
            new Attr("columnWidth", "dimension"),
            new Attr("numColumns", "integer",
                    new AttrEnum("auto_fit", "-1")
            ),
            /* ImageView */
            new Attr("scaleType", "",
                    new AttrEnum("matrix", "android.widget.ImageView.ScaleType.MATRIX"),
                    new AttrEnum("fitXY", "android.widget.ImageView.ScaleType.FIT_XY"),
                    new AttrEnum("fitStart", "android.widget.ImageView.ScaleType.FIT_START"),
                    new AttrEnum("fitCenter", "android.widget.ImageView.ScaleType.FIT_CENTER"),
                    new AttrEnum("fitEnd", "android.widget.ImageView.ScaleType.FIT_END"),
                    new AttrEnum("center", "android.widget.ImageView.ScaleType.CENTER"),
                    new AttrEnum("centerCrop", "android.widget.ImageView.ScaleType.CENTER_CROP"),
                    new AttrEnum("centerInside", "android.widget.ImageView.ScaleType.CENTER_INSIDE")
            ),
            new Attr("adjustViewBounds", "boolean"),
            new Attr("baselineAlignBottom", "boolean"),
            new Attr("cropToPadding", "boolean"),
            new Attr("baseline", "dimension"),
            new AttrDrawable("src",
                    "setImageDrawable",
                    null,
                    "setImageResource"
            ),
            new AttrDrawable("app:srcCompat",
                    "setImageDrawable",
                    null,
                    "setImageResource"
            ),
            new AttrTint("tint", "imageTint"),
            new Attr("tintMode", "", tintModes),
            /* ToggleButton */
            new Attr("textOn", "string"),
            new Attr("textOff", "string"),
            /* RelativeLayout */
            new Attr("ignoreGravity", "reference_id"),
            /* LinearLayout */
            new Attr("baselineAligned", "boolean"),
            new Attr("baselineAlignedChildIndex", "integer"),
            new Attr("weightSum", "float"),
            new Attr("measureWithLargestChild", "boolean", "measureWithLargestChildEnabled"),
            new Attr("showDividers", "integer",
                    new AttrEnum("none", "0"),
                    new AttrEnum("beginning", "1"),
                    new AttrEnum("middle", "2"),
                    new AttrEnum("end", "4")
            ),
            new Attr("dividerPadding", "dimension"),
            /* GridLayout */
            new Attr("rowCount", "integer"),
            new Attr("columnCount", "integer"),
            new Attr("useDefaultMargins", "boolean"),
            new Attr("rowOrderPreserved", "boolean"),
            new Attr("columnOrderPreserved", "boolean"),
            new Attr("alignmentMode", "",
                    new AttrEnum("alignBounds", "0"),
                    new AttrEnum("alignMargins", "1")
            ),
            /* ListView */
            new Attr("dividerHeight", "dimension"),
            new Attr("headerDividersEnabled", "boolean"),
            new Attr("footerDividersEnabled", "boolean"),
            new Attr("overScrollHeader", "drawable"),
            new Attr("overScrollFooter", "drawable"),
            /* ProgressView */
            new Attr("min", "integer"),
            new Attr("max", "integer"),
            new Attr("progress", "integer"),
            new Attr("secondaryProgress", "integer"),
            new Attr("indeterminate", "boolean"),
            new Attr("indeterminateOnly", "boolean", "indeterminate"),
            new Attr("indeterminateDrawable", "drawable"),
            new Attr("progressDrawable", "drawable"),
            new Attr("interpolator", "interpolator_id").needContext(),
            new AttrTint("progressTint"),
            new Attr("progressTintMode", "", tintModes),
            new AttrTint("progressBackgroundTint"),
            new Attr("progressBackgroundTintMode", "", tintModes),
            new AttrTint("secondaryProgressTint"),
            new Attr("secondaryProgressTintMode", "", tintModes),
            new AttrTint("indeterminateTint"),
            new Attr("indeterminateTintMode", "", tintModes),
            /* SeekBar */
            new Attr("thumbOffset", "dimension"),
            new Attr("splitTrack", "boolean"),
            new Attr("thumb", "drawable"),
            new AttrTint("thumbTint"),
            new Attr("thumbTintMode", "", tintModes),
            new Attr("tickMark", "drawable"),
            new AttrTint("tickMarkTint"),
            new Attr("tickMarkTintMode", "", tintModes),
            /* RatingBar */
            new Attr("numStars", "integer"),
            new Attr("rating", "float"),
            new Attr("stepSize", "float"),
            new Attr("isIndicator", "boolean"),
            /* RadioGroup */
            new Attr("checkedButton", "reference_id", "checkedId"),
            /* AutoCompleteTextView */
            new Attr("completionHint", "string"),
            new Attr("completionThreshold", "integer", "threshold"),
            new Attr("dropDownAnchor", "reference_id"),
            new Attr("dropDownWidth", "dimension",
                    new AttrEnum("fill_parent", "-1"),
                    new AttrEnum("match_parent", "-1"),
                    new AttrEnum("wrap_content", "-2")
            ),
            new Attr("dropDownHeight", "dimension",
                    new AttrEnum("fill_parent", "-1"),
                    new AttrEnum("match_parent", "-1"),
                    new AttrEnum("wrap_content", "-2")
            ),
            /* ViewAnimator */
            new Attr("inAnimation", "anim_id").needContext(),
            new Attr("outAnimation", "anim_id").needContext(),
            new Attr("animateFirstView", "boolean"),
            /* ViewFlipper */
            new Attr("flipInterval", "integer"),
            new Attr("autoStart", "boolean"),
            /* ScrollView */
            new Attr("fillViewport", "boolean"),
            new Attr("fillViewport", "boolean"),
            /* Spinner */
            new Attr("prompt", "string_id", "promptId"),
            new Attr("popupPromptView", "reference"),
            new AttrDrawable("popupBackground",
                    "setPopupBackgroundDrawable",
                    null,
                    "setPopupBackgroundResource"
            ),
            /* DatePicker */
            new Attr("firstDayOfWeek", "integer"),
            /* GestureOverlayView */
            new Attr("gestureStrokeWidth", "float"),
            new Attr("gestureColor", "color"),
            new Attr("uncertainGestureColor", "color"),
            new Attr("fadeOffset", "integer"),
            new Attr("fadeDuration", "integer"),
            new Attr("gestureStrokeType", "",
                    new AttrEnum("single", "0"),
                    new AttrEnum("multiple", "1")
            ),
            new Attr("gestureStrokeLengthThreshold", "float"),
            new Attr("gestureStrokeSquarenessThreshold", "float"),
            new Attr("gestureStrokeAngleThreshold", "float"),
            new Attr("eventsInterceptionEnabled", "boolean"),
            new Attr("fadeEnabled", "boolean"),
            /* CalendarView */
            new Attr("monthTextAppearance", "style_id"),
            new Attr("weekDayTextAppearance", "style_id"),
            new Attr("dateTextAppearance", "style_id"),
            new Attr("showWeekNumber", "boolean"),
            new Attr("shownWeekCount", "integer"),
            new Attr("selectedWeekBackgroundColor", "color"),
            new Attr("focusedMonthDateColor", "color"),
            new Attr("unfocusedMonthDateColor", "color"),
            new Attr("weekNumberColor", "color"),
            new Attr("weekSeparatorLineColor", "color"),
            new Attr("selectedDateVerticalBar", "drawable_id"),
            /* Switch */
            new Attr("track", "drawable"),
            new AttrTint("trackTint"),
            new Attr("trackTintMode", "", tintModes),
            new Attr("thumbTextPadding", "dimension"),
            new Attr("switchTextAppearance", "style_id"),
            new Attr("switchMinWidth", "dimension"),
            new Attr("switchPadding", "dimension"),
            new Attr("showText", "boolean"),
            /* Toolbar */
            new Attr("titleTextAppearance", "style_id"),
            new Attr("subtitleTextAppearance", "style_id"),
            new Attr("title", "string"),
            new Attr("subtitle", "string"),
            new Attr("titleMarginStart", "dimension"),
            new Attr("titleMarginEnd", "dimension"),
            new Attr("titleMarginTop", "dimension"),
            new Attr("titleMarginBottom", "dimension"),
            new Attr("contentInsetStartWithNavigation", "dimension"),
            new Attr("contentInsetEndWithActions", "dimension"),
            new Attr("collapseIcon", "drawable_id"),
            new Attr("collapseContentDescription", "string"),
            new Attr("navigationIcon", "drawable_id"),
            new Attr("navigationContentDescription", "string"),
            new Attr("logo", "drawable_id"),
            new Attr("logoDescription", "string"),
            new Attr("titleTextColor", "color"),
            new Attr("subtitleTextColor", "color"),
    };

    /*
            new Attr("digits", "string"),
            new Attr("phoneNumber", "boolean"),
            new Attr("inputMethod", "string"),
     */

    public static Attr[] textAttrs = {
            new Attr("text", "string"),
            new Attr("hint", "string"),
            new Attr("textAppearance", "style_id"),
            new Attr("textScaleX", "float"),
            new Attr("cursorVisible", "boolean"),
            new Attr("maxLines", "integer"),
            new Attr("lines", "integer"),
            new Attr("minLines", "integer"),
            new Attr("maxEms", "integer"),
            new Attr("ems", "integer"),
            new Attr("minEms", "integer"),
            new Attr("scrollHorizontally", "boolean", "horizontallyScrolling"),
            new Attr("password", "boolean"),
            new Attr("singleLine", "boolean"),
            new Attr("selectAllOnFocus", "boolean"),
            new Attr("includeFontPadding", "boolean"),
            new Attr("linksClickable", "boolean"),
            new Attr("freezesText", "boolean"),
            new Attr("drawablePadding", "dimension|integer", "compoundDrawablePadding"),
            new AttrTint("drawableTint"),
            new Attr("drawableTintMode", "",
                    new AttrEnum("src_over", "android.graphics.PorterDuff.Mode.SRC_OVER"),
                    new AttrEnum("src_in", "android.graphics.PorterDuff.Mode.SRC_IN"),
                    new AttrEnum("src_atop", "android.graphics.PorterDuff.Mode.SRC_ATOP"),
                    new AttrEnum("multiply", "android.graphics.PorterDuff.Mode.MULTIPLY"),
                    new AttrEnum("screen", "android.graphics.PorterDuff.Mode.SCREEN"),
                    new AttrEnum("add", "android.graphics.PorterDuff.Mode.ADD")
            ),
            new Attr("lineHeight", "dimension"),
            new Attr("firstBaselineToTopHeight", "dimension"),
            new Attr("lastBaselineToBottomHeight", "dimension"),
            new Attr("marqueeRepeatLimit", "integer",
                    new AttrEnum("marquee_forever", "-1")
            ),
            new Attr("privateImeOptions", "string"),
            new Attr("imeActionLabel", "string"),
            new Attr("fallbackLineSpacing", "boolean"),
            new Attr("breakStrategy", "",
                    new AttrEnum("simple", "0"),
                    new AttrEnum("high_quality", "1"),
                    new AttrEnum("balanced", "2")
            ),
            new Attr("hyphenationFrequency", "",
                    new AttrEnum("none", "0"),
                    new AttrEnum("normal", "1"),
                    new AttrEnum("full", "2")
            ),
            new Attr("justificationMode", "",
                    new AttrEnum("none", "0"),
                    new AttrEnum("inter_word", "1")
            ),
            new Attr("textColor", "color"),
            new Attr("textColorHighlight", "color"),
            new Attr("textColorHint", "color", "hintTextColor"),
            new Attr("textColorLink", "color", "linkTextColor"),
            new Attr("textCursorDrawable", "reference"),
            new Attr("textIsSelectable", "boolean"),
            new Attr("ellipsize", "",
                    new AttrEnum("none", "null"),
                    new AttrEnum("start", "android.text.TextUtils.TruncateAt.START"),
                    new AttrEnum("middle", "android.text.TextUtils.TruncateAt.MIDDLE"),
                    new AttrEnum("end", "android.text.TextUtils.TruncateAt.END"),
                    new AttrEnum("marquee", "android.text.TextUtils.TruncateAt.MARQUEE")
            ),

            new Attr("inputType", "",
                    new AttrEnum("none", "0x00000000"),
                    new AttrEnum("text", "0x00000001"),
                    new AttrEnum("textCapCharacters", "0x00001001"),
                    new AttrEnum("textCapWords", "0x00002001"),
                    new AttrEnum("textCapSentences", "0x00004001"),
                    new AttrEnum("textAutoCorrect", "0x00008001"),
                    new AttrEnum("textAutoComplete", "0x00010001"),
                    new AttrEnum("textMultiLine", "0x00020001"),
                    new AttrEnum("textImeMultiLine", "0x00040001"),
                    new AttrEnum("textNoSuggestions", "0x00080001"),
                    new AttrEnum("textUri", "0x00000011"),
                    new AttrEnum("textEmailAddress", "0x00000021"),
                    new AttrEnum("textEmailSubject", "0x00000031"),
                    new AttrEnum("textShortMessage", "0x00000041"),
                    new AttrEnum("textLongMessage", "0x00000051"),
                    new AttrEnum("textPersonName", "0x00000061"),
                    new AttrEnum("textPostalAddress", "0x00000071"),
                    new AttrEnum("textPassword", "0x00000081"),
                    new AttrEnum("textVisiblePassword", "0x00000091"),
                    new AttrEnum("textWebEditText", "0x000000a1"),
                    new AttrEnum("textFilter", "0x000000b1"),
                    new AttrEnum("textPhonetic", "0x000000c1"),
                    new AttrEnum("textWebEmailAddress", "0x000000d1"),
                    new AttrEnum("textWebPassword", "0x000000e1"),
                    new AttrEnum("number", "0x00000002"),
                    new AttrEnum("numberSigned", "0x00001002"),
                    new AttrEnum("numberDecimal", "0x00002002"),
                    new AttrEnum("numberPassword", "0x00000012"),
                    new AttrEnum("phone", "0x00000003"),
                    new AttrEnum("datetime", "0x00000004"),
                    new AttrEnum("date", "0x00000014"),
                    new AttrEnum("time", "0x00000024")
            ),
            new Attr("imeOptions", "",
                    new AttrEnum("normal", "0x00000000"),
                    new AttrEnum("actionUnspecified", "0x00000000"),
                    new AttrEnum("actionNone", "0x00000001"),
                    new AttrEnum("actionGo", "0x00000002"),
                    new AttrEnum("actionSearch", "0x00000003"),
                    new AttrEnum("actionSend", "0x00000004"),
                    new AttrEnum("actionNext", "0x00000005"),
                    new AttrEnum("actionDone", "0x00000006"),
                    new AttrEnum("actionPrevious", "0x00000007"),
                    new AttrEnum("flagNoPersonalizedLearning", "0x1000000"),
                    new AttrEnum("flagNoFullscreen", "0x2000000"),
                    new AttrEnum("flagNavigatePrevious", "0x4000000"),
                    new AttrEnum("flagNavigateNext", "0x8000000"),
                    new AttrEnum("flagNoExtractUi", "0x10000000"),
                    new AttrEnum("flagNoAccessoryAction", "0x20000000"),
                    new AttrEnum("flagNoEnterAction", "0x40000000"),
                    new AttrEnum("flagForceAscii", "0x80000000")
            ),
            new Attr("autoLink", "", "autoLinkMask",
                    new AttrEnum("none", "0x00"),
                    new AttrEnum("web", "0x01"),
                    new AttrEnum("email", "0x02"),
                    new AttrEnum("phone", "0x04"),
                    new AttrEnum("map", "0x08"),
                    new AttrEnum("all", "0x0f")
            ),
            new Attr("textSelectHandleLeft", "drawable"),
            new Attr("textSelectHandleRight", "drawable"),
            new Attr("textSelectHandle", "drawable"),

    };

    public static Attr[] constraintLayout = new Attr[]{
            new Attr("layout_optimizationLevel", "",
                    new AttrEnum("none", "0"),
                    new AttrEnum("standard", "7"),
                    new AttrEnum("direct", "1"), new
                    AttrEnum("barrier", "2"),
                    new AttrEnum("chains", "4"),
                    new AttrEnum("dimensions", "8"),
                    new AttrEnum("ratio", "16"),
                    new AttrEnum("groups", "32"),
                    new AttrEnum("graph", "64"),
                    new AttrEnum("graph_wrap", "128")
            )
    };

    public static Attr[] fragment = new Attr[]{
            new Attr("fragmentExitTransition", "transition_id"),
            new Attr("fragmentEnterTransition", "transition_id"),
            new Attr("fragmentSharedElementEnterTransition", "transition_id"),
            new Attr("fragmentReturnTransition", "transition_id"),
            new Attr("fragmentSharedElementReturnTransition", "transition_id"),
            new Attr("fragmentReenterTransition", "transition_id"),
            new Attr("fragmentAllowEnterTransitionOverlap", "boolean"),
            new Attr("fragmentAllowReturnTransitionOverlap", "boolean")
    };

    public static Attr[] cardView = new Attr[]{
            new Attr("app:cardBackgroundColor", "color"),
            new Attr("app:cardCornerRadius", "dimension|float", "radius"),
            new Attr("app:cardElevation", "dimension"),
            new Attr("app:cardMaxElevation", "dimension"),
            new Attr("app:cardUseCompatPadding", "boolean", "useCompatPadding"),
            new Attr("app:cardPreventCornerOverlap", "boolean", "preventCornerOverlap")
    };

    public static final HashMap<String, AttributesParser> parsers = new HashMap<>(500);

    static {
        load(customs);
        load(attrs);
        load(textAttrs);
        load(constraintLayout);
        load(fragment);
        load(cardView);
    }

    private static void load(AttributesParser[] p) {
        for (AttributesParser p0 : p) {
            String[] s = p0.getKeys();
            for (String s0 : s) {
                parsers.put(s0, p0);
            }
        }
    }

    /**
     * Includes an external parser with {@link XmlByPassAttr}
     */
    public static void include(XmlByPassAttr attr) {
        AttrEnum[] enums = new AttrEnum[attr.enums().length];
        for (int i = 0; i < enums.length; i++)
            enums[i] = new AttrEnum(attr.enums()[i].key(), attr.enums()[i].value());

        Attr a = new Attr(attr.name(), attr.format(), attr.codeName(), enums);
        if (attr.context())
            a.needContext();

        if (attr.ignoreAttr())
            parsers.remove(a.getName());
        else
            parsers.put(a.getName(), a);
    }
}
