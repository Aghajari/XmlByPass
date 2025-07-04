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

package com.aghajari.xmlbypass.includer;

public class IncludeLayout extends IncludeSource {

    public static final String NAME = "IncludeLayout";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    String getSource(String packageName) {
        return "package " + packageName + ";\n\n" +
                "import android.content.Context;\n" +
                "import android.util.AttributeSet;\n" +
                "import android.view.LayoutInflater;\n" +
                "import android.view.View;\n" +
                "import android.widget.FrameLayout;\n" +
                "\n" +
                "public class IncludeLayout extends FrameLayout {\n" +
                "    \n" +
                "    public IncludeLayout(Context context) {\n" +
                "        this(context, null);\n" +
                "    }\n" +
                "\n" +
                "    public IncludeLayout(Context context, AttributeSet attrs) {\n" +
                "        this(context, attrs, 0);\n" +
                "    }\n" +
                "\n" +
                "    public IncludeLayout(Context context, AttributeSet attrs, int defStyleAttr) {\n" +
                "        super(context, attrs, defStyleAttr);\n" +
                "    }\n" +
                "    \n" +
                "    public void loadLayout(int layoutId) {\n" +
                "        removeAllViews();\n" +
                "        View v = LayoutInflater.from(getContext()).inflate(layoutId, this, false);\n" +
                "        setLayoutParams(v.getLayoutParams());\n" +
                "        addView(v, -1, -1);\n" +
                "    }\n" +
                "}";
    }
}
