package com.gooosie.scancode.util;

import android.content.Context;
import android.widget.Toast;

/**
 * ToastUtil
 */

public class ToastUtil {
    public static void show(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
