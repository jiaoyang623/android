package org.daniel.android.workbanch.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import javax.microedition.khronos.opengles.GL10;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by daniel on 1/5/15.
 */

public class DeviceConfig {
    protected static final String LOG_TAG = DeviceConfig.class.getName();
    protected static final String UNKNOW = "Unknown";
    private static final String MOBILE_NETWORK = "2G/3G";
    private static final String WIFI = "Wi-Fi";
    public static final int DEFAULT_TIMEZONE = 8;

    public DeviceConfig() {
    }

    public static boolean isAppInstalled(String packageInfo, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean installed;

        try {
            pm.getPackageInfo(packageInfo, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }

        return installed;
    }

    public static boolean isChinese(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.toString().equals(Locale.CHINA.toString());
    }

    public static boolean isScreenPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static String getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int version_code = packageInfo.versionCode;
            return String.valueOf(version_code);
        } catch (PackageManager.NameNotFoundException e) {
            return UNKNOW;
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return UNKNOW;
        }
    }

    public static boolean checkPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        return pm.checkPermission(permission, context.getPackageName()) == 0;
    }

    public static String getAppLabel(Context context) {
        PackageManager pm = context.getPackageManager();

        ApplicationInfo applicationInfo;
        try {
            applicationInfo = pm.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }

        String applicationName = applicationInfo != null ? (String) pm.getApplicationLabel(applicationInfo) : "";
        return applicationName;
    }

    public static String[] getGPU(GL10 gl) {
        try {
            String[] buff = new String[2];
            String vendor = gl.glGetString(7936);
            String renderer = gl.glGetString(7937);
            buff[0] = vendor;
            buff[1] = renderer;
            return buff;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not read gpu infor:", e);
            return new String[0];
        }
    }

    public static String getCPU() {
        String cpuInfo = null;
        FileReader fstream = null;
        BufferedReader in = null;

        try {
            fstream = new FileReader("/proc/cpuinfo");
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    cpuInfo = in.readLine();
                    in.close();
                    fstream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Could not read from file /proc/cpuinfo", e);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "Could not open file /proc/cpuinfo", e);
        }

        if (cpuInfo != null) {
            int start = cpuInfo.indexOf(58) + 1;
            cpuInfo = cpuInfo.substring(start);
        }

        return cpuInfo.trim();
    }

    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            Log.w(LOG_TAG, "No IMEI.");
        }

        String imei = "";

        try {
            if (checkPermission(context, "android.permission.READ_PHONE_STATE")) {
                imei = tm.getDeviceId();
            }
        } catch (Exception e) {
            Log.w(LOG_TAG, "No IMEI.", e);
        }

        if (TextUtils.isEmpty(imei)) {
            Log.w(LOG_TAG, "No IMEI.");
            imei = getMac(context);
            if (TextUtils.isEmpty(imei)) {
                Log.w(LOG_TAG, "Failed to take mac as IMEI. Try to use Secure.ANDROID_ID instead.");
                imei = Settings.Secure.getString(context.getContentResolver(), "android_id");
                Log.i(LOG_TAG, "getDeviceId: Secure.ANDROID_ID: " + imei);
                return imei;
            }
        }

        return imei;
    }

    public static String getNetworkOperatorName(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager == null ? UNKNOW : telephonyManager.getNetworkOperatorName();
        } catch (Exception e) {
            e.printStackTrace();
            return UNKNOW;
        }
    }

    public static String getDisplayResolution(Context context) {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            String resolution = height + "*" + width;
            return resolution;
        } catch (Exception e) {
            e.printStackTrace();
            return UNKNOW;
        }
    }

    public static String[] getNetworkAccessMode(Context context) {
        String[] res = new String[]{UNKNOW, UNKNOW};

        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager.checkPermission("android.permission.ACCESS_NETWORK_STATE", context.getPackageName()) != 0) {
                res[0] = UNKNOW;
                return res;
            }

            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity == null) {
                res[0] = UNKNOW;
                return res;
            }

            NetworkInfo wifi_network = connectivity.getNetworkInfo(1);
            if (wifi_network.getState() == NetworkInfo.State.CONNECTED) {
                res[0] = WIFI;
                return res;
            }

            NetworkInfo mobile_network = connectivity.getNetworkInfo(0);
            if (mobile_network.getState() == NetworkInfo.State.CONNECTED) {
                res[0] = MOBILE_NETWORK;
                res[1] = mobile_network.getSubtypeName();
                return res;
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return res;
    }

    public static boolean isWiFiAvailable(Context context) {
        return WIFI.equals(getNetworkAccessMode(context)[0]);
    }

    public static Location getLocation(Context context) {
        return null;
    }

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null ? networkInfo.isConnectedOrConnecting() : false;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isSdCardWrittenable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static int getTimeZone(Context context) {
        try {
            Locale locale = getLocale(context);
            Calendar calendar = Calendar.getInstance(locale);
            if (calendar != null) {
                return calendar.getTimeZone().getRawOffset() / 3600000;
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "error in getTimeZone", e);
        }

        return DEFAULT_TIMEZONE;
    }

    /**
     * 0:country
     * 1:language
     */
    public static String[] getLocaleInfo(Context context) {
        String[] data = new String[2];

        try {
            Locale locale = getLocale(context);
            if (locale != null) {
                data[0] = locale.getCountry();
                data[1] = locale.getLanguage();
            }

            if (TextUtils.isEmpty(data[0])) {
                data[0] = UNKNOW;
            }

            if (TextUtils.isEmpty(data[1])) {
                data[1] = UNKNOW;
            }

            return data;
        } catch (Exception e) {
            Log.e(LOG_TAG, "error in getLocaleInfo", e);
            return data;
        }
    }

    private static Locale getLocale(Context context) {
        Locale locale = null;

        try {
            Configuration configuration = new Configuration();
            Settings.System.getConfiguration(context.getContentResolver(), configuration);
            if (configuration != null) {
                locale = configuration.locale;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "fail to read user config locale");
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;
    }

    public static String getMetaData(Context context, String key) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 128);
            if (info != null) {
                String value = info.metaData.getString(key);
                if (value != null) {
                    return value.trim();
                }

                Log.e(LOG_TAG, "Could not read " + key + " meta-data from AndroidManifest.xml.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not read " + key + " meta-data from AndroidManifest.xml.", e);
        }

        return null;
    }

    public static String getMac(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (checkPermission(context, "android.permission.ACCESS_WIFI_STATE")) {
                WifiInfo info = wifiManager.getConnectionInfo();
                return info.getMacAddress();
            }

            Log.w(LOG_TAG, "Could not get mac address.[no permission android.permission.ACCESS_WIFI_STATE");
        } catch (Exception e) {
            Log.w(LOG_TAG, "Could not get mac address." + e.toString());
        }

        return "";
    }

    public static int[] getResolutionArray(Context context) {
        String resolutin = getResolution(context);
        int[] array = new int[2];
        String[] strs = resolutin.split(",");
        array[0] = Integer.valueOf(strs[0]);
        array[1] = Integer.valueOf(strs[1]);

        return array;
    }

    public static String getResolution(Context context) {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int width = -1;
            int height = -1;
            if ((context.getApplicationInfo().flags & 8192) == 0) {
                width = reflectMetrics(displayMetrics, "noncompatWidthPixels");
                height = reflectMetrics(displayMetrics, "noncompatHeightPixels");
            }

            if (width == -1 || height == -1) {
                width = displayMetrics.widthPixels;
                height = displayMetrics.heightPixels;
            }

            StringBuffer buffer = new StringBuffer();
            buffer.append(width);
            buffer.append("*");
            buffer.append(height);
            return buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, "read resolution fail", e);
            return UNKNOW;
        }
    }

    private static int reflectMetrics(Object metrics, String field) {
        try {
            Field f = DisplayMetrics.class.getDeclaredField(field);
            f.setAccessible(true);
            return f.getInt(metrics);
        } catch (Exception var3) {
            var3.printStackTrace();
            return -1;
        }
    }

    public static String getNetworkCarrier(Context context) {
        try {
            return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
        } catch (Exception e) {
            Log.i(LOG_TAG, "read carrier fail", e);
            return UNKNOW;
        }
    }

    public static String getTimeString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String time = df.format(date);
        return time;
    }

    public static String getToday() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String time = df.format(date);
        return time;
    }

    public static Date toTime(String strDay) {
        try {
            SimpleDateFormat e = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = e.parse(strDay);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getIntervalSeconds(Date startTime, Date endTime) {
        if (startTime.after(endTime)) {
            Date date = startTime;
            startTime = endTime;
            endTime = date;
        }

        long start = startTime.getTime();
        long end = endTime.getTime();
        long duration = end - start;
        return (int) (duration / 1000L);
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static String getApplicationLable(Context context) {
        return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
    }

    public static boolean isDebug(Context context) {
        try {
            return (context.getApplicationInfo().flags & 2) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
