package com.gooosie.scancode.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.gooosie.scancode.R;
import com.gooosie.scancode.presenter.IMorePresenter;
import com.gooosie.scancode.presenter.MorePresenter;
import com.gooosie.scancode.util.StringAppendUtil;
import com.gooosie.scancode.util.ToastUtil;
import com.gooosie.scancode.view.IMoreView;

public class MoreActivity extends Activity implements IMoreView, View.OnClickListener {

    IMorePresenter mPresenter;

    private TextView mTextCopy;
    private TextView mTextSave;
    private TextView mTextMore;
    private TextView mTextDynamic;
    private View mDivider;
    private View mPaddingTop;
    private View mPaddingBottom;
    StringBuilder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new MorePresenter(this, this, getIntent());
        mPresenter.init();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_more);

        mTextCopy = (TextView) findViewById(R.id.text_btn_copy);
        mTextSave = (TextView) findViewById(R.id.text_btn_save);
        mTextMore = (TextView) findViewById(R.id.text_btn_more);
        mTextDynamic = (TextView) findViewById(R.id.text_btn_dynamic);
        mDivider = findViewById(R.id.view_divider_dynamic);
        mPaddingTop = findViewById (R.id.padding_top);
        mPaddingBottom = findViewById (R.id.padding_bottom);

        mTextCopy.setOnClickListener(this);
        mTextSave.setOnClickListener(this);
        mTextMore.setOnClickListener(this);
        mPaddingTop.setOnClickListener(this);
        mPaddingBottom.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.text_btn_copy:
                mPresenter.copyCode();
                break;
            case R.id.text_btn_save:
                mPresenter.saveCode();
                break;
            case R.id.text_btn_more:
                mPresenter.loadMore();
                break;
            case R.id.padding_top:
            case R.id.padding_bottom:
                finish();
                break;
        }
    }

    @Override
    public void showCopySuccess() {
        ToastUtil.show(this, getString(R.string.toast_copy_success));
    }

    @Override
    public void showSaveSuccess() {
        ToastUtil.show(this, getString(R.string.toast_save_success));
    }

    @Override
    public void showSaveFailed() {
        ToastUtil.show(this, getString(R.string.toast_save_failed));
    }

    @Override
    public void showBasicDetail(String format, String content) {
        mTextMore.setClickable(false);
        mTextMore.setGravity(Gravity.START);
        mBuilder = new StringBuilder(42);
        StringAppendUtil.appendDetail(mBuilder, getString(R.string.more_format), format);
        StringAppendUtil.appendDetail(mBuilder, getString(R.string.more_content), content);
        mTextMore.setText(mBuilder.toString());
    }

    @Override
    public void appendDetail(String message) {
        if (mBuilder == null) {
            mBuilder = new StringBuilder(42);
        }
        mBuilder.append(message);
        mTextMore.setText(mBuilder.toString());
    }

    @Override
    public void showDynamicButton(String title, final Intent intent) {
        mTextDynamic.setVisibility(View.VISIBLE);
        mDivider.setVisibility(View.VISIBLE);
        mTextDynamic.setText(title);
        mTextDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }
}
