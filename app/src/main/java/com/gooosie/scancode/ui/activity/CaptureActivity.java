package com.gooosie.scancode.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.zxing.Result;
import com.gooosie.scancode.R;
import com.gooosie.scancode.presenter.CapturePresenter;
import com.gooosie.scancode.presenter.ICapturePresenter;
import com.gooosie.scancode.ui.fragment.CaptureFragment;
import com.gooosie.scancode.util.CodeUtil;
import com.gooosie.scancode.util.DisplayUtil;
import com.gooosie.scancode.view.ICaptureView;

public class CaptureActivity extends Activity implements ICaptureView, View.OnClickListener, CodeUtil.AnalyzeCallback {

    private static final String TAG = CaptureActivity.class.getCanonicalName();

    private ICapturePresenter mPresenter;

    private ImageButton mImgBtnGallery;
    private ImageButton mImgBtnFlashlight;
    private TextView mTextBtnAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CapturePresenter(this, this);
        mPresenter.init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.resetFlashlight();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_capture);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenHeightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(this, dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(this, dm.heightPixels);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        CaptureFragment captureFragment = new CaptureFragment();
        captureFragment.setAnalyzeCallback(this);
        getFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, captureFragment).commit();

        mImgBtnGallery = (ImageButton) findViewById(R.id.img_btn_gallery);
        mImgBtnFlashlight = (ImageButton) findViewById(R.id.img_btn_flashlight);
        mTextBtnAbout = (TextView) findViewById(R.id.text_btn_about);

        mImgBtnGallery.setOnClickListener(this);
        mImgBtnFlashlight.setOnClickListener(this);
        mTextBtnAbout.setOnClickListener(this);
    }

    @Override
    public void onFlashlightOn() {
        mImgBtnFlashlight.setColorFilter(0xFFFFFFFF);
    }

    @Override
    public void onFlashlightOff() {
        mImgBtnFlashlight.clearColorFilter();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.img_btn_gallery:
                mPresenter.openGallery();
                break;
            case R.id.img_btn_flashlight:
                mPresenter.toggleFlashlight();
                break;
            case R.id.text_btn_about:
                mPresenter.openAbout();
            default:
                break;
        }
    }

    @Override
    public void onAnalyzeSuccess(Bitmap bitmap, Result result) {
        Log.d(TAG, "onAnalyzeSuccess: " + result.getBarcodeFormat());
        Log.d(TAG, "onAnalyzeSuccess: " + result.getText());
        mPresenter.onAnalyzeSuccess(bitmap, result);
    }

    @Override
    public void onAnalyzeFailed() {
        Log.d(TAG, "onAnalyzeFailed: " + "Analyze failed.");
        mPresenter.onAnalyzeFailed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.handleResult(requestCode, resultCode, data);
    }
}
