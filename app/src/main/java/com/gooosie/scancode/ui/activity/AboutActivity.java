package com.gooosie.scancode.ui.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gooosie.scancode.BuildConfig;
import com.gooosie.scancode.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        String version = "v" + BuildConfig.VERSION_NAME;
        TextView tvVersion = (TextView) findViewById(R.id.about_version);
        tvVersion.setText(version);

        TextView tvBack = (TextView) findViewById(R.id.text_btn_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
