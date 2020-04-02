package com.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.R;
import com.blankj.utilcode.util.ActivityUtils;

public class BaseActivity extends AppCompatActivity {
    private ImageView toolbar_back;
    private TextView toolbar_title;
    private WebView mWebview;
    private ProgressBar pb;
    private SwipeRefreshLayout refreshLayout;

    protected void initToolBar(boolean isShowBack,String title){
        toolbar_back=findViewById(R.id.toolbar_back);
        toolbar_title=findViewById(R.id.toolbar_title);
        toolbar_back.setVisibility(isShowBack? View.VISIBLE:View.GONE);
        toolbar_title.setText(title);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void  initWebView(final String url){
        refreshLayout=findViewById(R.id.swipe_fresh);
        mWebview = findViewById(R.id.webView);
        pb=findViewById(R.id.progressBar);
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        WebSettings seting=mWebview.getSettings();
        seting.setJavaScriptEnabled(true);//设置webview支持javascript脚本
        mWebview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    pb.setVisibility(View.GONE);//加载完网页进度条消失
                }
                else{
                    pb.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pb.setProgress(newProgress);//设置进度值
                }

            }
        });
        mWebview.loadUrl(url);

        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebview.loadUrl(mWebview.getUrl());
                refreshLayout.setRefreshing(false);
            }
        });

        refreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
                return mWebview.getScrollY() > 0;
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Activity activityByContext = ActivityUtils.getActivityByContext(this);
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mWebview!=null&&mWebview.canGoBack()) {
                mWebview.goBack(); //goBack()表示返回WebView的上一页面
                return true;
            } else {
                if("activity.IndexActivity".equals(activityByContext.getLocalClassName())){
                    ActivityUtils.startHomeActivity();
                    return true;
                }else{
                    finish();
                    return true;
                }
            }

        }
        return false;
    }
}
