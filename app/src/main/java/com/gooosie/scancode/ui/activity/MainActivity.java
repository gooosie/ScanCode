package com.gooosie.scancode.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.gooosie.scancode.R;
import com.gooosie.scancode.presenter.IMainPresenter;
import com.gooosie.scancode.presenter.MainPresenter;
import com.gooosie.scancode.util.ToastUtil;
import com.gooosie.scancode.view.IMainView;

public class MainActivity extends Activity implements IMainView, View.OnClickListener {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private IMainPresenter mPresenter;

    private ImageView mImgCode;
    private EditText mEditTextCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new MainPresenter(this, this);

        mPresenter.init();
        mPresenter.gotoCaptureActivity();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mImgCode = (ImageView) findViewById(R.id.img_code);
        mEditTextCode = (EditText) findViewById(R.id.edit_text_code);

        mImgCode.setOnClickListener(this);
        mEditTextCode.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.handleResult(requestCode,  resultCode, data);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_code:
                mPresenter.gotoMoreActivity();
                break;
            case R.id.edit_text_code:
                mPresenter.copyContent(mEditTextCode.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void showCode(String content, Bitmap codeBmp) {
        mImgCode.setImageBitmap(codeBmp);
        mEditTextCode.setText(content);
    }

    @Override
    public void showCopySuccess() {
        ToastUtil.show(this, getString(R.string.toast_copy_success));
    }

    @Override
    public void showDecodeFailed() {
        ToastUtil.show(this, getString(R.string.toast_decode_failed));
    }
}
