package com.gooosie.scancode.presenter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.gooosie.scancode.R;
import com.gooosie.scancode.ui.activity.CaptureActivity;
import com.gooosie.scancode.ui.activity.MainActivity;
import com.gooosie.scancode.ui.activity.MoreActivity;
import com.gooosie.scancode.util.CodeUtil;
import com.gooosie.scancode.util.DisplayUtil;
import com.gooosie.scancode.util.ImageUtil;
import com.gooosie.scancode.view.IMainView;
import com.gooosie.scancode.zxing.decode.Intents;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * MainPresenter
 */

public class MainPresenter implements IMainPresenter {

    private static final String TAG = MainPresenter.class.getCanonicalName();
    private static final int REQUEST_CAPTURE = 100;

    private String mContent;
    private BarcodeFormat mFormat;
    private Bitmap mCodeBmp;

    private int mCodeSize = 0;
    private long mCodeContentClickTime = 0;

    private Activity mActivity;
    private IMainView mView;

    public MainPresenter(MainActivity activity, IMainView view) {
        mActivity = activity;
        mView = view;
    }

    @Override
    public void init() {
        mCodeSize = (int) mActivity.getResources().getDimension(R.dimen.code_size);

        mView.initView();
    }

    @Override
    public void gotoCaptureActivity() {
        mActivity.startActivityForResult(new Intent(mActivity, CaptureActivity.class), REQUEST_CAPTURE);
    }

    @Override
    public void gotoMoreActivity() {
        Intent intent = new Intent(mActivity, MoreActivity.class);
        intent.putExtra(CodeUtil.EXTRA_CONTENT, mContent);
        intent.putExtra(CodeUtil.EXTRA_FORMAT, mFormat.toString());
        mActivity.startActivity(intent);
    }

    @Override
    public void handleResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            mActivity.finish();
        }
        // 从扫码界面返回
        if (requestCode == REQUEST_CAPTURE) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }

            int result = bundle.getInt(CodeUtil.RESULT_TYPE);
            if (result == CodeUtil.RESULT_SUCCESS) {
                mContent = bundle.getString(Intents.Scan.RESULT);
                mFormat = BarcodeFormat.valueOf(bundle.getString(Intents.Scan.RESULT_FORMAT));
                mCodeBmp = CodeUtil.createImage(mContent, mFormat, mCodeSize, mCodeSize);
                Log.d(TAG, "handleResult: " + mContent + " " + mFormat);
                mView.showCode(mContent, mCodeBmp);
            } else if (result == CodeUtil.RESULT_FAILED) {
                mView.showDecodeFailed();
                gotoCaptureActivity();
            }
        }
    }

    @Override
    public void copyContent(String content) {
        if (System.currentTimeMillis() - mCodeContentClickTime >= 2000L) {
            ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText("Code", content);
            clipboard.setPrimaryClip(data);
            mView.showCopySuccess();
            mCodeContentClickTime = System.currentTimeMillis();
        }
    }
}
