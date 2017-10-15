package com.gooosie.scancode.presenter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.gooosie.scancode.R;
import com.gooosie.scancode.code.CodeHandlerFactory;
import com.gooosie.scancode.code.HandleCodeCallback;
import com.gooosie.scancode.code.ICodeHandler;
import com.gooosie.scancode.util.CodeUtil;
import com.gooosie.scancode.view.IMoreView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * MorePresenter
 */

public class MorePresenter implements IMorePresenter {

    private static final String TAG = MorePresenter.class.getCanonicalName();

    private static final String SAVE_IMG_PATH = "/Pictures/QRCode";

    private Activity mActivity;
    private IMoreView mView;
    private Intent mIntent;
    private ICodeHandler mCodeHandler;

    private long mCopyClickTime = 0;
    private long mSaveClickTime = 0;
    private String mContent;
    private BarcodeFormat mFormat;
    private int mCodeSize = 0;
    private String mExternalStoragePath;

    public MorePresenter(Activity activity, IMoreView view, Intent intent) {
        mActivity = activity;
        mView = view;
        mIntent = intent;
    }

    @Override
    public void init() {
        if (mIntent != null) {
            mContent = mIntent.getStringExtra(CodeUtil.EXTRA_CONTENT);
            mFormat = BarcodeFormat.valueOf(mIntent.getStringExtra(CodeUtil.EXTRA_FORMAT));
            mCodeHandler = CodeHandlerFactory.getCodeHandler(mFormat);
        }
        mCodeSize = (int) mActivity.getResources().getDimension(R.dimen.code_size);
        mExternalStoragePath = Environment.getExternalStorageDirectory().getPath();

        mView.initView();

        analyzeCode();
    }

    @Override
    public void copyCode() {
        if (System.currentTimeMillis() - mCopyClickTime >= 2000L) {
            ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText("Code", mContent);
            clipboard.setPrimaryClip(data);
            mView.showCopySuccess();
            mCopyClickTime = System.currentTimeMillis();
        }
    }

    @Override
    public void saveCode() {
        if (System.currentTimeMillis() - mSaveClickTime >= 2000L) {
            // 保存图片
            if (TextUtils.isEmpty(mExternalStoragePath)) {
                Log.d(TAG, "saveCode: External storage path not found.");
                mView.showSaveFailed();
            }

            File dir = new File(mExternalStoragePath + SAVE_IMG_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            DateFormat format = SimpleDateFormat.getDateTimeInstance();
            String date = format.format(System.currentTimeMillis());

            String fileName = mExternalStoragePath + SAVE_IMG_PATH + "/" + date;
            File file = new File(fileName + ".png");

            int i = 1;
            while (file.exists()) {
                file = new File(fileName + "(" + i++ + ")" + ".png");
            }

            FileOutputStream output = null;
            try {
                file.createNewFile();
                output = new FileOutputStream(file);
                Bitmap bitmap = CodeUtil.createImage(mContent, mFormat, mCodeSize, mCodeSize);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    Log.d(TAG, "saveCode: Save at " + file.getName());
                    mView.showSaveSuccess();

                    mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                }
            } catch (IOException e) {
                Log.e(TAG, "saveImage: " + e, e);
                mView.showSaveFailed();
            } finally {
                if (output != null) {
                    try {
                        output.flush();
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            mSaveClickTime = System.currentTimeMillis();
        }
    }

    @Override
    public void loadMore() {
        mView.showBasicDetail(mFormat.toString(), mContent);
        if (mCodeHandler != null) {
            mCodeHandler.requestMore(mContent, new HandleCodeCallback() {
                @Override
                public void onHandled(final String result) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mView.appendDetail(result);
                        }
                    });
                }
            });
        }
    }

    private void analyzeCode() {
        if (mContent == null || mFormat == null) {
            return;
        }

        switch (mFormat) {
            case EAN_13:
                String baseUriString = "https://www.amazon.cn/s/?field-keywords=";
                mView.showDynamicButton(mActivity.getString(R.string.dynamic_amazon), createIntent(baseUriString + mContent));
                break;
            case QR_CODE:
                if (mContent.startsWith("http://") || mContent.startsWith("https://")) {
                    mView.showDynamicButton(mActivity.getString(R.string.dynamic_access), createIntent(mContent));
                } else if (mContent.startsWith("ss://")) {
                    mView.showDynamicButton(mActivity.getString(R.string.dynamic_shadowsocks), createIntent(mContent));
                }
                break;
            default:
                break;
        }
    }

    private Intent createIntent(String uriString) {
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        return intent;
    }
}
