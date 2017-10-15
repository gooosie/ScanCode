package com.gooosie.scancode.presenter;

import android.content.Intent;
import android.graphics.Bitmap;

import com.google.zxing.Result;

/**
 * ICapturePresenter
 */

public interface ICapturePresenter extends IPresenter{
    void openGallery();
    void toggleFlashlight();
    void resetFlashlight();
    void onAnalyzeSuccess(Bitmap bitmap, Result result);
    void onAnalyzeFailed();
    void openAbout();
    void handleResult(int requestCode, int resultCode, Intent data);
}
