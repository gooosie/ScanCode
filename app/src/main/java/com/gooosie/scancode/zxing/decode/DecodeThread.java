package com.gooosie.scancode.zxing.decode;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.gooosie.scancode.ui.fragment.CaptureFragment;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * DecodeThread
 */

final class DecodeThread extends Thread {

    private static final String TAG = DecodeThread.class.getCanonicalName();

    public static final String BARCODE_BITMAP = "barcode_bitmap";

    private final CaptureFragment mFragment;
    private final Hashtable<DecodeHintType, Object> mHints;
    private Handler mHandler;
    private final CountDownLatch mHandlerInitLatch;

    DecodeThread(CaptureFragment fragment,
                 Vector<BarcodeFormat> decodeFormats,
                 String characterSet,
                 ResultPointCallback resultPointCallback) {
        mFragment = fragment;
        mHandlerInitLatch = new CountDownLatch(1);
        mHints = new Hashtable<>(3);

        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<>();
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }

        mHints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        if (characterSet != null) {
            mHints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }

        mHints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            mHandlerInitLatch.await();
        } catch (InterruptedException e) {
            Log.d(TAG, "getHandler: " + e);
        }

        return mHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        mHandler = new DecodeHandler(mFragment, mHints);
        mHandlerInitLatch.countDown();
        Looper.loop();
    }
}
