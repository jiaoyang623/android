//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.daniel.android.workbanch.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Helper {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public Helper() {
    }

    public static String md5(String content) {
        if (content == null) {
            return null;
        } else {
            try {
                byte[] bytes = content.getBytes();
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.reset();
                messageDigest.update(bytes);
                byte[] result = messageDigest.digest();
                StringBuffer buffer = new StringBuffer();

                for (int i = 0; i < result.length; ++i) {
                    buffer.append(String.format("%02X", Byte.valueOf(result[i])));
                }

                return buffer.toString();
            } catch (Exception var6) {
                return content.replaceAll("[^[a-z][A-Z][0-9][.][_]]", "");
            }
        }
    }

    public static String getFileMD5(File file) {
        MessageDigest messageDigest;
        FileInputStream fileInputStream;
        byte[] buffer = new byte[1024];

        try {
            if (!file.isFile()) {
                return "";
            }

            messageDigest = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);

            while (true) {
                int size;
                if ((size = fileInputStream.read(buffer, 0, 1024)) == -1) {
                    fileInputStream.close();
                    break;
                }

                messageDigest.update(buffer, 0, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger result = new BigInteger(1, messageDigest.digest());
        return String.format("%1$032x", result);
    }

    public static String formatFileSize(long length) {
        String result;
        if (length < 1024L) {
            result = length + "B";
        } else {
            DecimalFormat format;
            if (length < 1024L * 1024L) {
                format = new DecimalFormat("#0.00");
                result = format.format((double) ((float) length) / 1024.0D) + "K";
            } else if (length < 1024L * 1024L * 1024L) {
                format = new DecimalFormat("#0.00");
                result = format.format(length / (1024.0 * 1024.0)) + "M";
            } else {
                format = new DecimalFormat("#0.00");
                result = format.format(length / (1024.0 * 1024.0 * 1024.0)) + "G";
            }
        }

        return result;
    }

    public static String formatFileSize(String size) {
        try {
            return formatFileSize(Long.valueOf(size).longValue());
        } catch (NumberFormatException e) {
            return size;
        }
    }

    public static void openApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    public static boolean openUrlSchema(Context context, String url) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEmpty(String content) {
        if (content == null || content.length() == 0) {
            return true;
        } else {
            int len = content.length();
            for (int i = 0; i < len; i++) {
                if (Character.isWhitespace(content.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isAbsoluteUrl(String url) {
        if (isEmpty(url)) {
            return false;
        } else {
            String lowerUrl = url.trim().toLowerCase(Locale.US);
            return lowerUrl.startsWith("http://") || lowerUrl.startsWith("https://");
        }
    }

    public static String getDate() {
        Date date = new Date();
        return getTime(date);
    }

    public static String getTime(Date date) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            return format.format(date);
        }
    }

    public static String readString(InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        char[] buffer = new char[1024];
        StringWriter writer = new StringWriter();

        int count;
        while (-1 != (count = reader.read(buffer))) {
            writer.write(buffer, 0, count);
        }

        return writer.toString();
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        int count;
        while (-1 != (count = inputStream.read(buffer))) {
            byteArrayOutputStream.write(buffer, 0, count);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static void writeFile(File file, byte[] data) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        try {
            fileOutputStream.write(data);
            fileOutputStream.flush();
        } finally {
            closeStream(fileOutputStream);
        }

    }

    public static void writeFile(File file, String data) throws IOException {
        writeFile(file, data.getBytes());
    }

    public static void closeStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public static JSONObject mapToJson(Map<String, String> map) {
        JSONObject object = new JSONObject();

        if (map != null && map.size() > 0) {
            Set keySet = map.keySet();
            Iterator<String> iterator = keySet.iterator();

            while (iterator.hasNext()) {
                String key = iterator.next();
                try {
                    object.put(key, map.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return object;
    }
}
