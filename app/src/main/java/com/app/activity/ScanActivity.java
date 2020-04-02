package com.app.activity;

import android.content.Intent;
import android.os.Bundle;

import com.app.R;


public class ScanActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Intent it=getIntent();
        String scan_url=it.getStringExtra("url");
        initWebView(scan_url);
    }
}
