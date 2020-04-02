package com.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IndexActivity extends BaseActivity {
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    List<Fragment> fragments;
    MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        SharedPreferences preferences = getSharedPreferences("system", Context.MODE_PRIVATE);
        String appVersion = preferences.getString("appVersion", "");
        //检查更新
        if (Double.parseDouble(appVersion) > SystemUtil.getAppVersion(IndexActivity.this)) {
            File file = new File(getCacheDir(), "ysf.apk");
            String sys_md5 = "14480fc08932105d55b9217c6d2fb90b";
            //检查MD5
            String md5 = SystemUtil.getFileMd5(file);
            if (!TextUtils.isEmpty(md5) && sys_md5.equals(md5)) {
                //安装
                SystemUtil.installApk(IndexActivity.this, file);
            } else {
                UpdateVersionDialog.show(IndexActivity.this, appVersion);
            }
        }

        getScheme();

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
                    initWebView(marketingUrl03);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private class FragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments;

        private FragmentAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
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
    private String TAG="===========";
    private void getScheme() {
        Intent intent = getIntent();
        String action = intent.getAction();
        Log.i(TAG, "action:" + action);
        Uri uri = intent.getData();
        if (uri != null) {
            // 完整的url信息
            String url = uri.toString();
            Log.i(TAG, "url:" + uri);

            // scheme部分
            String scheme1 = uri.getScheme();
            Log.i(TAG, "scheme:" + scheme1);

            // host部分
            String host = uri.getHost();
            Log.i(TAG, "host:" + host);

            // port部分
            int port = uri.getPort();
            Log.i(TAG, "port:" + port);

            // 访问路劲
            String path = uri.getPath();
            Log.i(TAG, "path:" + path);

            List<String> pathSegments = uri.getPathSegments();

            // Query部分
            String query = uri.getQuery();
            Log.i(TAG, "query:" + query);

            //获取指定参数值
            String success = uri.getQueryParameter("query1");
            Log.i(TAG, "success:" + success);
        }
    }

}
