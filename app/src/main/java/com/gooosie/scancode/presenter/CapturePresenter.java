package com.gooosie.scancode.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;
import com.gooosie.scancode.R;
import com.gooosie.scancode.ui.activity.AboutActivity;
import com.gooosie.scancode.ui.activity.CaptureActivity;
import com.gooosie.scancode.util.CodeUtil;
import com.gooosie.scancode.util.ImageUtil;
import com.gooosie.scancode.view.ICaptureView;
import com.gooosie.scancode.zxing.decode.Intents;

/**
 * CapturePresenter
 */

public class CapturePresenter implements ICapturePresenter {

    private static final int REQUEST_IMAGE = 1;

    Activity mActivity;
    ICaptureView mView;

    private boolean hasFlashlightOn = false;

    public CapturePresenter(Activity activity, ICaptureView view) {
        mActivity = activity;
        mView = view;
    }

    @Override
    public void init() {
        mView.initView();
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        mActivity.startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void toggleFlashlight() {
        if (!CodeUtil.setLightEnable(!hasFlashlightOn)) {
            Toast.makeText(mActivity, R.string.toast_flashlight_unsupport, Toast.LENGTH_LONG).show();
            return;
        }
        hasFlashlightOn = !hasFlashlightOn;
        if (hasFlashlightOn) {
            mView.onFlashlightOn();
        } else {
            mView.onFlashlightOff();
        }
    }

    @Override
    public void resetFlashlight() {
        mView.onFlashlightOff();
        hasFlashlightOn = false;
    }

    @Override
    public void onAnalyzeSuccess(Bitmap bitmap, Result result) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(CodeUtil.RESULT_TYPE, CodeUtil.RESULT_SUCCESS);
        bundle.putString(Intents.Scan.RESULT, result.getText());
        bundle.putString(Intents.Scan.RESULT_FORMAT, result.getBarcodeFormat().name());
        resultIntent.putExtras(bundle);
        mActivity.setResult(Activity.RESULT_OK, resultIntent);
        mActivity.finish();
    }

    @Override
    public void onAnalyzeFailed() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(CodeUtil.RESULT_TYPE, CodeUtil.RESULT_FAILED);
        bundle.putString(Intents.Scan.RESULT, "");
        resultIntent.putExtras(bundle);
        mActivity.setResult(Activity.RESULT_OK, resultIntent);
        mActivity.finish();
    }

    @Override
    public void openAbout() {
        mActivity.startActivity(new Intent(mActivity, AboutActivity.class));
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }

            Uri uri = data.getData();
            CodeUtil.analyzeBitmap(ImageUtil.getImageAbsolutePath(mActivity, uri), (CaptureActivity) mActivity);
        }
    }
}
