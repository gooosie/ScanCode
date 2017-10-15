package com.gooosie.scancode.zxing.decode;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.gooosie.scancode.R;
import com.gooosie.scancode.ui.fragment.CaptureFragment;
import com.gooosie.scancode.zxing.camera.CameraManager;
import com.gooosie.scancode.zxing.view.ViewfinderResultPointCallback;
import com.gooosie.scancode.zxing.view.ViewfinderView;

import java.util.Vector;

/**
 * CaptureFragmentHandler
 */

public final class CaptureFragmentHandler extends Handler {

    private static final String TAG = CaptureFragmentHandler.class.getCanonicalName();

    private final CaptureFragment mFragment;
    private final DecodeThread mDecodeThread;
    private State mState;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    public CaptureFragmentHandler(CaptureFragment fragment, Vector<BarcodeFormat> decodeFormats,
                                  String characterSet, ViewfinderView viewfinderView) {
        mFragment = fragment;
        mDecodeThread = new DecodeThread(fragment, decodeFormats, characterSet,
                new ViewfinderResultPointCallback(viewfinderView));
        mDecodeThread.start();
        mState = State.SUCCESS;
        // Start ourselves capturing previews and decoding.
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.auto_focus) {
            //Log.d(TAG, "Got auto-focus message");
            // When one auto focus pass finishes, start another. This is the closest thing to
            // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
            if (mState == State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        } else if (message.what == R.id.restart_preview) {
            Log.d(TAG, "Got restart preview message");
            restartPreviewAndDecode();
        } else if (message.what == R.id.decode_succeeded) {
            Log.d(TAG, "Got decode succeeded message");
            mState = State.SUCCESS;
            Bundle bundle = message.getData();

            /***********************************************************************/
            Bitmap barcode = bundle == null ? null :
                    (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);

            mFragment.handleDecode((Result) message.obj, barcode);
            /***********************************************************************/
        } else if (message.what == R.id.decode_failed) {
            // We're decoding as fast as possible, so when one decode fails, start another.
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
        } else if (message.what == R.id.return_scan_result) {
            Log.d(TAG, "Got return scan result message");
            mFragment.getActivity().setResult(Activity.RESULT_OK, (Intent) message.obj);
            mFragment.getActivity().finish();
        } else if (message.what == R.id.launch_product_query) {
            Log.d(TAG, "Got product query message");
            String url = (String) message.obj;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            mFragment.getActivity().startActivity(intent);
        }
    }

    public void quitSynchronously() {
        mState = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (mState == State.SUCCESS) {
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            mFragment.drawViewfinder();
        }
    }
}
