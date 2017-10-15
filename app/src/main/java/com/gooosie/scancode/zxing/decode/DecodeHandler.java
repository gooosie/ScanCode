package com.gooosie.scancode.zxing.decode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.gooosie.scancode.R;
import com.gooosie.scancode.ui.fragment.CaptureFragment;
import com.gooosie.scancode.zxing.camera.CameraManager;
import com.gooosie.scancode.zxing.camera.PlanarYUVLuminanceSource;

import java.util.Hashtable;

/**
 * DecodeHandler
 */

final class DecodeHandler extends Handler {

    private static final String TAG = DecodeHandler.class.getCanonicalName();

    private final CaptureFragment mFragment;
    private final MultiFormatReader mMultiFormatReader;

    public DecodeHandler(CaptureFragment fragment, Hashtable<DecodeHintType, Object> hints) {
        mMultiFormatReader = new MultiFormatReader();
        mMultiFormatReader.setHints(hints);
        mFragment = fragment;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == R.id.decode) {
            decode((byte[])msg.obj, msg.arg1, msg.arg2);
        } else if (msg.what == R.id.quit) {
            Looper.myLooper().quit();
        }
    }

    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        Result rawResult = null;

        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }

        int tmp = width;
        width = height;
        height = tmp;

        PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData, width, height);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = mMultiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
            // continue
        } finally {
            mMultiFormatReader.reset();
        }

        if (rawResult != null) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
            Message message = Message.obtain(mFragment.getHandler(), R.id.decode_succeeded, rawResult);
            Bundle bundle = new Bundle();
            bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
            message.setData(bundle);
            Log.d(TAG, "Sending decode succeeded message...");
            message.sendToTarget();
        } else {
            Message message = Message.obtain(mFragment.getHandler(), R.id.decode_failed);
            message.sendToTarget();
        }
    }
}
