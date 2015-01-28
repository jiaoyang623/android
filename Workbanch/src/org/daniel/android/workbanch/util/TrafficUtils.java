//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.daniel.android.workbanch.util;

import android.content.Context;

import java.lang.reflect.Method;

public class TrafficUtils {

    /**
     * 获取流量信息<br/>
     *
     * @return [download, upload]
     */
    public static long[] getTrafficData(Context context) {
        try {
            Class clazz = Class.forName("android.net.TrafficStats");
            Method methodGetUidRxBytes = clazz.getMethod("getUidRxBytes", new Class[]{Integer.TYPE});
            Method methodTetUidTxBytes = clazz.getMethod("getUidTxBytes", new Class[]{Integer.TYPE});
            int uid = context.getApplicationInfo().uid;
            if (uid == -1) {
                return null;
            } else {
                long[] trafficData = new long[]{
                        ((Long) methodGetUidRxBytes.invoke(null, new Object[]{Integer.valueOf(uid)})).longValue(),
                        ((Long) methodTetUidTxBytes.invoke(null, new Object[]{Integer.valueOf(uid)})).longValue()
                };
                return trafficData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
