package com.gooosie.scancode.util;

import android.text.TextUtils;

/**
 * StringAppendUtil
 */

public class StringAppendUtil {
    public static void appendDetail(StringBuilder builder, String title, String content) {
        if (builder == null || TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            return;
        }

        builder.append(title).append(": ").append("\n").append(content).append("\n\n");
    }
}
