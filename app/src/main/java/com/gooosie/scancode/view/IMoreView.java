package com.gooosie.scancode.view;

import android.content.Intent;

/**
 * IMoreView
 */

public interface IMoreView extends IView {
    void showCopySuccess();
    void showSaveSuccess();
    void showSaveFailed();
    void showBasicDetail(String format, String content);
    void appendDetail(String message);
    void showDynamicButton(String title, Intent intent);
}
