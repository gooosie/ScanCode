package com.gooosie.scancode.presenter;

import android.content.Intent;

/**
 * IMainPresenter
 */

public interface IMainPresenter extends IPresenter {
    void gotoCaptureActivity();
    void gotoMoreActivity();
    void handleResult(int requestCode, int resultCode, Intent data);
    void copyContent(String content);
}
