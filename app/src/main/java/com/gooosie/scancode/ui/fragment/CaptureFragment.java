package com.gooosie.scancode.ui.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.gooosie.scancode.R;
import com.gooosie.scancode.util.CodeUtil;
import com.gooosie.scancode.zxing.camera.CameraManager;
import com.gooosie.scancode.zxing.decode.CaptureFragmentHandler;
import com.gooosie.scancode.zxing.decode.InactivityTimer;
import com.gooosie.scancode.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

/**
 * CaptureFragment
 */

public class CaptureFragment extends Fragment implements SurfaceHolder.Callback {

    private static final String TAG = CaptureFragment.class.getCanonicalName();

    private CaptureFragmentHandler mHandler;
    private ViewfinderView mViewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> mDecodeFormats;
    private String mCharacterSet;
    private InactivityTimer mInactivityTimer;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private CodeUtil.AnalyzeCallback mAnalyzeCallback;
    private Camera mCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CameraManager.init(getActivity().getApplication());

        hasSurface = false;
        mInactivityTimer = new InactivityTimer(getActivity());
        mCharacterSet = "UTF-8";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        View view = null;
        if (bundle != null) {
            int layoutId = bundle.getInt(CodeUtil.LAYOUT_ID);
            if (layoutId != -1) {
                view = inflater.inflate(layoutId, null);
            }
        }

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_capture, null);
        }

        mViewfinderView = view.findViewById(R.id.viewfinder_view);
        mSurfaceView = view.findViewById(R.id.preview_view);
        mSurfaceHolder = mSurfaceView.getHolder();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface) {
            initCamera(mSurfaceHolder);
        } else {
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mDecodeFormats = null;
        mCharacterSet = null;
    }

    @Override
    public void onPause() {
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        CameraManager.get().closeDriver();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mInactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        mInactivityTimer.onActivity();

        if (result == null || TextUtils.isEmpty(result.getText())) {
            if (mAnalyzeCallback != null) {
                mAnalyzeCallback.onAnalyzeFailed();
            }
        } else {
            if (mAnalyzeCallback != null) {
                mAnalyzeCallback.onAnalyzeSuccess(barcode, result);
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            mCamera = CameraManager.get().getCamera();
        } catch (IOException | RuntimeException e) {
            return;
        }
        if (mHandler == null) {
            mHandler = new CaptureFragmentHandler(this, mDecodeFormats, mCharacterSet, mViewfinderView);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(mSurfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        hasSurface = false;
        if (mCamera != null && CameraManager.get().isPreviewing()) {
            if (!CameraManager.get().isUseOneShotPreviewCallback()) {
                mCamera.setPreviewCallback(null);
            }
            mCamera.stopPreview();
            CameraManager.get().getPreviewCallback().setHandler(null, 0);
            CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
            CameraManager.get().setPreviewing(false);
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();
    }

    public CodeUtil.AnalyzeCallback getAnalyzeCallback() {
        return mAnalyzeCallback;
    }

    public void setAnalyzeCallback(CodeUtil.AnalyzeCallback analyzeCallback) {
        mAnalyzeCallback = analyzeCallback;
    }
}
