package com.example.fibath.classes.util;

import java.util.Locale;

public final class StringUtils {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[(bytes.length * 2)];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 255;
            int i = j * 2;
            char[] cArr = HEX_ARRAY;
            hexChars[i] = cArr[v >>> 4];
            hexChars[(j * 2) + 1] = cArr[v & 15];
        }
        return new String(hexChars);
    }

    public static String capitalizeFirstChar(String s) {
        Locale locale = Locale.getDefault();
        if (s == null || s.length() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(s.substring(0, 1).toUpperCase(locale));
        sb.append(s.substring(1).toLowerCase());
        return sb.toString();
    }
}
