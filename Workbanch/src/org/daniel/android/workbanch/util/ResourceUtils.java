//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.daniel.android.workbanch.util;

import android.content.Context;
import android.content.res.Resources;
import u.aly.Log;

public class ResourceUtils {
    private static final String className = ResourceUtils.class.getName();
    private static ResourceUtils instance = null;
    private Resources resource;
    private final String defPackage;

    private ResourceUtils(Context context) {
        this.resource = context.getResources();
        this.defPackage = context.getPackageName();
    }

    public static synchronized ResourceUtils getInstance(Context context) {
        if (instance == null) {
            instance = new ResourceUtils(context.getApplicationContext());
        }

        return instance;
    }

    public int getAnim(String name) {
        return this.getIdentifier(name, "anim");
    }

    public int getId(String name) {
        return this.getIdentifier(name, "id");
    }

    public int getDrawable(String name) {
        return this.getIdentifier(name, "drawable");
    }

    public int getLayout(String name) {
        return this.getIdentifier(name, "layout");
    }

    public int getStyle(String name) {
        return this.getIdentifier(name, "style");
    }

    public int getString(String name) {
        return this.getIdentifier(name, "string");
    }

    public int getArray(String name) {
        return this.getIdentifier(name, "array");
    }

    private int getIdentifier(String name, String defType) {
        int identifier = this.resource.getIdentifier(name, defType, this.defPackage);
        if (identifier == 0) {
            Log.b(className, "getRes(" + defType + "/ " + name + ")");
            Log.b(className, "Error getting resource. Make sure you have copied all resources (res/) from SDK to your project. ");
            return 0;
        } else {
            return identifier;
        }
    }
}
