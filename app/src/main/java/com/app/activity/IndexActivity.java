package com.app.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.R;
import com.app.fragment.HomeFragment;
import com.app.fragment.PersonalFragment;
import com.app.fragment.PreferentiallFragment;
import com.app.update.UpdateVersionDialog;
import com.app.util.SystemUtil;
import com.blankj.utilcode.util.ActivityUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IndexActivity extends BaseActivity {
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    List<Fragment> fragments;
    MenuItem menuItem;
    WebView mWebview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        SharedPreferences preferences = getSharedPreferences("system", Context.MODE_PRIVATE);
        String appVersion = preferences.getString("appVersion", "");
        //检查更新
        if (Double.parseDouble(appVersion) > SystemUtil.getAppVersion(IndexActivity.this)) {
            File file=new File(getCacheDir(),"ysf.apk");
            String sys_md5="14480fc08932105d55b9217c6d2fb90b";
            //检查MD5
            String md5=SystemUtil.getFileMd5(file);
            if(!TextUtils.isEmpty(md5)&&sys_md5.equals(md5)){
                //安装
                SystemUtil.installApk(IndexActivity.this, file);
            }else{
                UpdateVersionDialog.show(IndexActivity.this,appVersion);
            }
        }

        //设置底部导航
        viewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new PreferentiallFragment());
        fragments.add(new PersonalFragment());
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.homeItem:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.preferentialItem:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.personalItem:
                        viewPager.setCurrentItem(2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        final String marketingUrl03 = preferences.getString("marketingUrl03", "");
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem == null) {
                    menuItem = bottomNavigationView.getMenu().getItem(0);
                }
                //将上次的选择设置为false,等待下次选择
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
                if (position == 1) {
                    mWebview = findViewById(R.id.webView);
                    mWebview.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return false;
                        }
                    });
                    mWebview.loadUrl(marketingUrl03);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private class FragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments;

        public FragmentAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mWebview!=null&&mWebview.canGoBack()) {
                mWebview.goBack(); //goBack()表示返回WebView的上一页面
                return true;
            } else {
                ActivityUtils.startHomeActivity();
                return true;
            }

        }
        return false;
    }

}
