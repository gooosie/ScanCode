package com.gooosie.scancode.view;

import android.graphics.Bitmap;

/**
 * IMainView
 */

public interface IMainView extends IView {
    void showCode(String content, Bitmap codeBmp);
    void showCopySuccess();
    void showDecodeFailed();
}
