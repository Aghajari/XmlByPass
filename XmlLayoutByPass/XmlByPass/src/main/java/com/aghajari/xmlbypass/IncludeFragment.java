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

import java.io.PrintWriter;

import javax.tools.JavaFileObject;

public class IncludeFragment {

    public String packageName;

    public static String getName(){
        return "IncludeFragment";
    }

    public String getFullName() {
        return packageName + "." + getName();
    }

    public void write(XmlByPassProcessor processor){
        try {
            JavaFileObject object = processor.getProcessingEnv().getFiler().createSourceFile(getFullName());
            PrintWriter pw = new PrintWriter(object.openWriter());
            pw.write(getSource());
            pw.close();
        } catch (Exception e) {
            processor.error(e.getMessage());
        }
    }

    private String getSource() {
        return "package " + packageName + ";\n\n" +
                "import android.content.Context;\n" +
                "import android.content.ContextWrapper;\n" +
                "import android.transition.TransitionInflater;\n" +
                "import android.util.AttributeSet;\n" +
                "import android.widget.FrameLayout;\n" +
                "\n" +
                "import androidx.fragment.app.Fragment;\n" +
                "import androidx.fragment.app.FragmentActivity;\n" +
                "import androidx.fragment.app.FragmentManager;\n" +
                "import androidx.fragment.app.FragmentTransaction;\n" +
                "\n" +
                "public class IncludeFragment extends FrameLayout {\n" +
                "\n" +
                "    private final static int UNSET = 0;\n" +
                "\n" +
                "    private int fragmentExitTransition = UNSET;\n" +
                "    private int fragmentEnterTransition = UNSET;\n" +
                "    private int fragmentSharedElementEnterTransition = UNSET;\n" +
                "    private int fragmentReturnTransition = UNSET;\n" +
                "    private int fragmentSharedElementReturnTransition = UNSET;\n" +
                "    private int fragmentReenterTransition = UNSET;\n" +
                "    private boolean fragmentAllowEnterTransitionOverlap;\n" +
                "    private boolean fragmentAllowReturnTransitionOverlap;\n" +
                "\n" +
                "    public IncludeFragment(Context context) {\n" +
                "        super(context);\n" +
                "    }\n" +
                "\n" +
                "    public IncludeFragment(Context context, AttributeSet attrs) {\n" +
                "        super(context, attrs);\n" +
                "    }\n" +
                "\n" +
                "    public IncludeFragment(Context context, AttributeSet attrs, int defStyleAttr) {\n" +
                "        super(context, attrs, defStyleAttr);\n" +
                "    }\n" +
                "\n" +
                "    public void showFragment(Class<? extends Fragment> fragment) {\n" +
                "        try {\n" +
                "            FragmentActivity activity = asActivity(getContext());\n" +
                "            FragmentManager fm = activity.getSupportFragmentManager();\n" +
                "            FragmentTransaction ft = fm.beginTransaction()\n" +
                "                    .setReorderingAllowed(true);\n" +
                "\n" +
                "            if (fragmentAllowEnterTransitionOverlap ||\n" +
                "                    fragmentAllowReturnTransitionOverlap ||\n" +
                "                    fragmentEnterTransition != UNSET ||\n" +
                "                    fragmentExitTransition != UNSET ||\n" +
                "                    fragmentReenterTransition != UNSET ||\n" +
                "                    fragmentReturnTransition != UNSET ||\n" +
                "                    fragmentSharedElementEnterTransition != UNSET ||\n" +
                "                    fragmentSharedElementReturnTransition != UNSET) {\n" +
                "                \n" +
                "                Fragment frag = fm.getFragmentFactory()\n" +
                "                        .instantiate(getContext().getClassLoader(), fragment.getName());\n" +
                "                frag.setAllowEnterTransitionOverlap(fragmentAllowEnterTransitionOverlap);\n" +
                "                frag.setAllowReturnTransitionOverlap(fragmentAllowReturnTransitionOverlap);\n" +
                "\n" +
                "                TransitionInflater inflater = TransitionInflater.from(getContext());\n" +
                "                if (fragmentEnterTransition != UNSET)\n" +
                "                    frag.setEnterTransition(inflater.inflateTransition(fragmentEnterTransition));\n" +
                "                if (fragmentExitTransition != UNSET)\n" +
                "                    frag.setExitTransition(inflater.inflateTransition(fragmentExitTransition));\n" +
                "                if (fragmentReenterTransition != UNSET)\n" +
                "                    frag.setReenterTransition(inflater.inflateTransition(fragmentReenterTransition));\n" +
                "                if (fragmentReturnTransition != UNSET)\n" +
                "                    frag.setReturnTransition(inflater.inflateTransition(fragmentReturnTransition));\n" +
                "                if (fragmentSharedElementEnterTransition != UNSET)\n" +
                "                    frag.setSharedElementEnterTransition(inflater.inflateTransition(fragmentSharedElementEnterTransition));\n" +
                "                if (fragmentSharedElementReturnTransition != UNSET)\n" +
                "                    frag.setSharedElementReturnTransition(inflater.inflateTransition(fragmentSharedElementReturnTransition));\n" +
                "                \n" +
                "                ft.add(getId(), frag, getTag() == null ? null : getTag().toString());\n" +
                "            } else {\n" +
                "                //noinspection unchecked\n" +
                "                ft.add(getId(), fragment,\n" +
                "                        null, getTag() == null ? null : getTag().toString());\n" +
                "            }\n" +
                "            ft.commitNowAllowingStateLoss();\n" +
                "        } catch (Exception e) {\n" +
                "            e.printStackTrace();\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public int getFragmentExitTransition() {\n" +
                "        return fragmentExitTransition;\n" +
                "    }\n" +
                "\n" +
                "    public void setFragmentExitTransition(int fragmentExitTransition) {\n" +
                "        this.fragmentExitTransition = fragmentExitTransition;\n" +
                "    }\n" +
                "\n" +
                "    public int getFragmentEnterTransition() {\n" +
                "        return fragmentEnterTransition;\n" +
                "    }\n" +
                "\n" +
                "    public void setFragmentEnterTransition(int fragmentEnterTransition) {\n" +
                "        this.fragmentEnterTransition = fragmentEnterTransition;\n" +
                "    }\n" +
                "\n" +
                "    public int getFragmentSharedElementEnterTransition() {\n" +
                "        return fragmentSharedElementEnterTransition;\n" +
                "    }\n" +
                "\n" +
                "    public void setFragmentSharedElementEnterTransition(int fragmentSharedElementEnterTransition) {\n" +
                "        this.fragmentSharedElementEnterTransition = fragmentSharedElementEnterTransition;\n" +
                "    }\n" +
                "\n" +
                "    public int getFragmentReturnTransition() {\n" +
                "        return fragmentReturnTransition;\n" +
                "    }\n" +
                "\n" +
                "    public void setFragmentReturnTransition(int fragmentReturnTransition) {\n" +
                "        this.fragmentReturnTransition = fragmentReturnTransition;\n" +
                "    }\n" +
                "\n" +
                "    public int getFragmentSharedElementReturnTransition() {\n" +
                "        return fragmentSharedElementReturnTransition;\n" +
                "    }\n" +
                "\n" +
                "    public void setFragmentSharedElementReturnTransition(int fragmentSharedElementReturnTransition) {\n" +
                "        this.fragmentSharedElementReturnTransition = fragmentSharedElementReturnTransition;\n" +
                "    }\n" +
                "\n" +
                "    public int getFragmentReenterTransition() {\n" +
                "        return fragmentReenterTransition;\n" +
                "    }\n" +
                "\n" +
                "    public void setFragmentReenterTransition(int fragmentReenterTransition) {\n" +
                "        this.fragmentReenterTransition = fragmentReenterTransition;\n" +
                "    }\n" +
                "\n" +
                "    public boolean isFragmentAllowEnterTransitionOverlap() {\n" +
                "        return fragmentAllowEnterTransitionOverlap;\n" +
                "    }\n" +
                "\n" +
                "    public void setFragmentAllowEnterTransitionOverlap(boolean fragmentAllowEnterTransitionOverlap) {\n" +
                "        this.fragmentAllowEnterTransitionOverlap = fragmentAllowEnterTransitionOverlap;\n" +
                "    }\n" +
                "\n" +
                "    public boolean isFragmentAllowReturnTransitionOverlap() {\n" +
                "        return fragmentAllowReturnTransitionOverlap;\n" +
                "    }\n" +
                "\n" +
                "    public void setFragmentAllowReturnTransitionOverlap(boolean fragmentAllowReturnTransitionOverlap) {\n" +
                "        this.fragmentAllowReturnTransitionOverlap = fragmentAllowReturnTransitionOverlap;\n" +
                "    }\n" +
                "\n" +
                "    protected FragmentActivity asActivity(Context context) {\n" +
                "        Context result = context;\n" +
                "\n" +
                "        while (result instanceof ContextWrapper) {\n" +
                "            if (result instanceof FragmentActivity)\n" +
                "                return (FragmentActivity) result;\n" +
                "\n" +
                "            result = ((ContextWrapper) result).getBaseContext();\n" +
                "        }\n" +
                "\n" +
                "        throw new IllegalArgumentException(\"The passed Context is not an FragmentActivity.\");\n" +
                "    }\n" +
                "}";
    }
}
