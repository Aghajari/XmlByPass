package com.aghajari.xmlbypass.includer;

public class IncludeViewStub extends IncludeSource {

    public final static String ORIGINAL_CLASS_NAME = "ViewStub";
    public final static String ORIGINAL_FULL_NAME = "android.view.ViewStub";
    public final static String NAME = "XmlByPassViewStub";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    String getSource(String packageName) {
        return "package " + packageName + ";\n\n" +
                "import android.annotation.SuppressLint;\n" +
                "import android.content.Context;\n" +
                "import android.graphics.Canvas;\n" +
                "import android.view.View;\n" +
                "import android.view.ViewGroup;\n" +
                "import android.view.ViewParent;\n" +
                "\n" +
                "import java.lang.ref.WeakReference;\n" +
                "\n" +
                "@SuppressLint(\"ViewConstructor\")\n" +
                "public class XmlByPassViewStub extends View {\n" +
                "\n" +
                "    private int mInflatedId = NO_ID;\n" +
                "    private int mLayoutResource;\n" +
                "\n" +
                "    private WeakReference<View> mInflatedViewRef;\n" +
                "\n" +
                "    private final XmlByPassLayoutInflater mInflater;\n" +
                "    private OnInflateListener mInflateListener;\n" +
                "\n" +
                "    public XmlByPassViewStub(Context context, XmlByPassLayoutInflater inflater) {\n" +
                "        super(context);\n" +
                "\n" +
                "        mInflater = inflater;\n" +
                "        setVisibility(GONE);\n" +
                "        setWillNotDraw(true);\n" +
                "    }\n" +
                "\n" +
                "    public int getInflatedId() {\n" +
                "        return mInflatedId;\n" +
                "    }\n" +
                "\n" +
                "    public void setInflatedId(int inflatedId) {\n" +
                "        mInflatedId = inflatedId;\n" +
                "    }\n" +
                "\n" +
                "    public int getLayoutResource() {\n" +
                "        return mLayoutResource;\n" +
                "    }\n" +
                "\n" +
                "    public void setLayoutResource(int layoutResource) {\n" +
                "        mLayoutResource = layoutResource;\n" +
                "    }\n" +
                "\n" +
                "    public void setOnInflateListener(OnInflateListener inflateListener) {\n" +
                "        mInflateListener = inflateListener;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {\n" +
                "        setMeasuredDimension(0, 0);\n" +
                "    }\n" +
                "\n" +
                "    @SuppressLint(\"MissingSuperCall\")\n" +
                "    @Override\n" +
                "    public void draw(Canvas canvas) {\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    protected void dispatchDraw(Canvas canvas) {\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void setVisibility(int visibility) {\n" +
                "        if (mInflatedViewRef != null) {\n" +
                "            View view = mInflatedViewRef.get();\n" +
                "            if (view != null) {\n" +
                "                view.setVisibility(visibility);\n" +
                "            } else {\n" +
                "                throw new IllegalStateException(\"setVisibility called on un-referenced view\");\n" +
                "            }\n" +
                "        } else {\n" +
                "            super.setVisibility(visibility);\n" +
                "            if (visibility == VISIBLE || visibility == INVISIBLE) {\n" +
                "                inflate();\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    public View inflate() {\n" +
                "        final ViewParent viewParent = getParent();\n" +
                "\n" +
                "        if (viewParent instanceof ViewGroup) {\n" +
                "            if (mLayoutResource != 0) {\n" +
                "                final ViewGroup parent = (ViewGroup) viewParent;\n" +
                "                final View view = mInflater.inflate();\n" +
                "\n" +
                "                if (mInflatedId != NO_ID) {\n" +
                "                    view.setId(mInflatedId);\n" +
                "                }\n" +
                "                replaceSelfWithView(view, parent);\n" +
                "\n" +
                "                mInflatedViewRef = new WeakReference<>(view);\n" +
                "                if (mInflateListener != null) {\n" +
                "                    mInflateListener.onInflate(this, view);\n" +
                "                }\n" +
                "\n" +
                "                return view;\n" +
                "            } else {\n" +
                "                throw new IllegalArgumentException(\"ViewStub must have a valid layoutResource\");\n" +
                "            }\n" +
                "        } else {\n" +
                "            throw new IllegalStateException(\"ViewStub must have a non-null ViewGroup viewParent\");\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private void replaceSelfWithView(View view, ViewGroup parent) {\n" +
                "        final int index = parent.indexOfChild(this);\n" +
                "        parent.removeViewInLayout(this);\n" +
                "\n" +
                "        final ViewGroup.LayoutParams layoutParams = getLayoutParams();\n" +
                "        if (layoutParams != null) {\n" +
                "            parent.addView(view, index, layoutParams);\n" +
                "        } else {\n" +
                "            parent.addView(view, index);\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    @FunctionalInterface\n" +
                "    public interface OnInflateListener {\n" +
                "        void onInflate(XmlByPassViewStub stub, View inflated);\n" +
                "    }\n" +
                "\n" +
                "    @FunctionalInterface\n" +
                "    public interface XmlByPassLayoutInflater {\n" +
                "        View inflate();\n" +
                "    }\n" +
                "}";
    }
}
