//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.daniel.android.workbanch.util;

import java.io.*;

public class SerializableUtils {
    public static String objectToString(Serializable data) {
        if (data == null) {
            return "";
        } else {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(data);
                objectOutputStream.close();
                return byteToString(byteArrayOutputStream.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static Object stringToObject(String StringData) {
        if (StringData != null && StringData.length() != 0) {
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stringToByte(StringData));
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                return objectInputStream.readObject();
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String byteToString(byte[] byteData) {
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < byteData.length; ++i) {
            stringBuffer.append((char) ((byteData[i] >> 4 & 15) + 97));
            stringBuffer.append((char) ((byteData[i] & 15) + 97));
        }

        return stringBuffer.toString();
    }

    public static byte[] stringToByte(String stringData) {
        byte[] byteData = new byte[stringData.length() / 2];

        for (int i = 0; i < stringData.length(); i += 2) {
            char c = stringData.charAt(i);
            byteData[i / 2] = (byte) (c - 97 << 4);
            c = stringData.charAt(i + 1);
            byteData[i / 2] = (byte) (byteData[i / 2] + (c - 97));
        }

        return byteData;
    }
}
