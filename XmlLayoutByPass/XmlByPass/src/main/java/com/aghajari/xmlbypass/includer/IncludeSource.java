package com.aghajari.xmlbypass.includer;

import com.aghajari.xmlbypass.XmlByPassProcessor;

import java.io.PrintWriter;

import javax.tools.JavaFileObject;

public abstract class IncludeSource {

    private String packageName;
    private boolean attached;

    public abstract String getName();

    abstract String getSource(String packageName);

    public String getFullName() {
        return packageName + "." + getName();
    }

    public String getNameForPackage(String packageName) {
        if (packageName.equals(this.packageName)) {
            return getName();
        } else {
            return getFullName();
        }
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void write(XmlByPassProcessor processor) {
        if (!attached) {
            return;
        }

        try {
            JavaFileObject object = processor.getProcessingEnv().getFiler().createSourceFile(getFullName());
            PrintWriter pw = new PrintWriter(object.openWriter());
            pw.write(getSource(packageName));
            pw.close();
        } catch (Exception e) {
            processor.error(e.getMessage());
        }
    }

    public void attach() {
        attached = true;
    }

    public void detach() {
        attached = false;
    }
}
