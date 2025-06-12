package com.aghajari.xmlbypass.includer;

public class IncludeOnClick extends IncludeSource {

    public final static String NAME = "XmlByPassOnClickListener";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    String getSource(String packageName) {
        return "package " + packageName + ";\n\n" +
                "import android.content.Context;\n" +
                "import android.content.ContextWrapper;\n" +
                "import android.view.View;\n" +
                "\n" +
                "import java.lang.reflect.InvocationTargetException;\n" +
                "import java.lang.reflect.Method;\n" +
                "\n" +
                "public class XmlByPassOnClickListener implements View.OnClickListener {\n" +
                "    private final String mMethodName;\n" +
                "\n" +
                "    private Method mResolvedMethod;\n" +
                "    private Context mResolvedContext;\n" +
                "\n" +
                "    public XmlByPassOnClickListener(String methodName) {\n" +
                "        mMethodName = methodName;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void onClick(View view) {\n" +
                "        if (mResolvedMethod == null) {\n" +
                "            resolveMethod(view);\n" +
                "        }\n" +
                "\n" +
                "        try {\n" +
                "            mResolvedMethod.invoke(mResolvedContext, view);\n" +
                "        } catch (IllegalAccessException e) {\n" +
                "            throw new IllegalStateException(\n" +
                "                    \"Could not execute non-public method for android:onClick\", e);\n" +
                "        } catch (InvocationTargetException e) {\n" +
                "            throw new IllegalStateException(\n" +
                "                    \"Could not execute method for android:onClick\", e);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private void resolveMethod(View view) {\n" +
                "        Context context = view.getContext();\n" +
                "        while (context != null) {\n" +
                "            try {\n" +
                "                if (!context.isRestricted()) {\n" +
                "                    final Method method = context.getClass().getMethod(mMethodName, View.class);\n" +
                "                    if (method != null) {\n" +
                "                        mResolvedMethod = method;\n" +
                "                        mResolvedContext = context;\n" +
                "                        return;\n" +
                "                    }\n" +
                "                }\n" +
                "            } catch (NoSuchMethodException e) {\n" +
                "                // Failed to find method, keep searching up the hierarchy.\n" +
                "            }\n" +
                "\n" +
                "            if (context instanceof ContextWrapper) {\n" +
                "                context = ((ContextWrapper) context).getBaseContext();\n" +
                "            } else {\n" +
                "                // Can't search up the hierarchy, null out and fail.\n" +
                "                context = null;\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        final int id = view.getId();\n" +
                "        final String idText = id == View.NO_ID ? \"\" : \" with id '\"\n" +
                "                + view.getContext().getResources().getResourceEntryName(id) + \"'\";\n" +
                "        throw new IllegalStateException(\"Could not find method \" + mMethodName\n" +
                "                + \"(View) in a parent or ancestor Context for android:onClick \"\n" +
                "                + \"attribute defined on view \" + view.getClass() + idText);\n" +
                "    }\n" +
                "}";
    }
}
