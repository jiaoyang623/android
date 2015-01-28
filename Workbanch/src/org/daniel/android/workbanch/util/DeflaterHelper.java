package org.daniel.android.workbanch.util;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by daniel on 1/5/15.
 */

public class DeflaterHelper {
    public static int TOTAL_LEN;

    public DeflaterHelper() {
    }

    public static byte[] deflaterCompress(String str, String charEncoding) throws IOException {
        return TextUtils.isEmpty(str) ? null : deflaterCompress(str.getBytes(charEncoding));
    }

    public static byte[] deflaterCompress(byte[] bin) throws IOException {
        if (bin != null && bin.length > 0) {
            Deflater deflater = new Deflater();
            deflater.setInput(bin);
            deflater.finish();
            byte[] buf = new byte[8192];
            TOTAL_LEN = 0;
            ByteArrayOutputStream outputStream = null;

            try {
                outputStream = new ByteArrayOutputStream();

                while (true) {
                    if (deflater.finished()) {
                        deflater.end();
                        break;
                    }

                    int compressedBytes = deflater.deflate(buf);
                    TOTAL_LEN += compressedBytes;
                    outputStream.write(buf, 0, compressedBytes);
                }
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }

            }

            byte[] compressedBytes1 = outputStream.toByteArray();
            return compressedBytes1;
        } else {
            return null;
        }
    }

    public static String deflaterDecompress(byte[] input, String charEncoding) throws UnsupportedEncodingException, DataFormatException {
        byte[] array = deflaterDecompress(input);
        return array != null ? new String(array, charEncoding) : null;
    }

    public static byte[] deflaterDecompress(byte[] input) throws UnsupportedEncodingException, DataFormatException {
        if (input != null && input.length != 0) {
            Inflater inflater = new Inflater();
            inflater.setInput(input, 0, input.length);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            boolean len = false;

            int len1;
            for (int count = 0; !inflater.needsInput(); count += len1) {
                len1 = inflater.inflate(buffer);
                outputStream.write(buffer, count, len1);
            }

            inflater.end();
            return outputStream.toByteArray();
        } else {
            return null;
        }
    }
}
