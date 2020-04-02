package com.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.Manifest;
import com.app.R;
import com.app.util.ApplicationUtil;
import com.app.util.JsonUtil;
import com.app.util.OkHttpUtil;
import com.app.util.StringUtil;
import com.app.util.SystemUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private int reclen = 3;
    private TextView skip;
    Timer timer = new Timer();
    private Handler handler;
    private Runnable runnable;
    Map<String, String> res;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.activity_main);
        requestPower();
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    skip.setText(reclen + "");
                    reclen--;
                    if (reclen < 0) {
                        skip.setText("跳过");
                        timer.cancel();
                    }
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip:
                if (reclen < 0) {
                    if (res != null && res.get("respCd").equals("0000")) {
                        Intent it = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(it);
                    }
                    finish();
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * * 需要进行检测的权限数组 这里只列举了几项 小伙伴可以根据自己的项目需求 来添加
     *    
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,//定位权限
            Manifest.permission.ACCESS_FINE_LOCATION,//定位权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储卡写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//存储卡读取权限
            Manifest.permission.READ_PHONE_STATE,//读取手机状态权限
            Manifest.permission.CAMERA,//读取手机相机权限
            Manifest.permission.RECORD_AUDIO
    };

    public void requestPower() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //判断是否已经赋予权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, needPermissions, 1);
            } else {
                enterMainPage();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    // 以前是!b
                    if (b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else {
                        finish();
                    }
                } else {
                    enterMainPage();
                }
            }
        }
        //权限管理
        if (requestCode == 123) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 提示用户去应用设置界面手动开启权限
     */
    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许应用使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
                    }
                }).setCancelable(false).show();
    }

    /**
     * 跳转到当前应用的设置界面
     */
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }

    private void enterMainPage() {
        SharedPreferences preferences = getSharedPreferences("system", Context.MODE_PRIVATE);
        final String vid = preferences.getString("vid", "");

        Map<String, String> body = new HashMap<>();
        body.put("apkChnl", "apk");
        body.put("appVersion", "1.0");
        body.put("deviceId", SystemUtil.getIMEI(MainActivity.this));
        body.put("deviceMode", SystemUtil.getSystemModel());
        body.put("osType", "0");
        body.put("osVersion", SystemUtil.getSystemVersion());

        Map<String, String> head = new HashMap<>();
        if (vid != null && vid != "") {
            head.put("vid", vid);
        }
        String url=ApplicationUtil.getProperties(getApplicationContext(), "url") + "1000";
        OkHttpUtil.getInstance().postJsonAsyn(url, head, body, new OkHttpUtil.NetCall() {
            @Override
            public void success(String body) {
                res = JsonUtil.fromJson(body, Map.class);
                Log.e("Response",res.toString());
                SharedPreferences preferences = getSharedPreferences("system", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("vid", res.get("vid"));
                editor.putString("servicePhone", res.get("servicePhone"));
                editor.putString("marketingUrl01", res.get("marketingUrl01"));
                editor.putString("marketingUrl02", res.get("marketingUrl02"));
                editor.putString("marketingUrl03", res.get("marketingUrl03"));
                editor.putString("appVersion", res.get("appVersion"));
                editor.putString("appDownlaodUrl", res.get("appDownlaodUrl"));
                editor.commit();
            }

            @Override
            public void failed(Call call, IOException e) {
                return;
            }
        });


        SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String mobile = sp.getString("mobile", "");
        if (StringUtil.isNotBlank(mobile)) {
            Intent it = new Intent(MainActivity.this, IndexActivity.class);
            startActivity(it);
            finish();
        } else {
            skip = findViewById(R.id.skip);
            skip.setOnClickListener(this);
            timer.schedule(task, 1000, 1000);
            handler = new Handler();
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    if (res != null && res.get("respCd").equals("0000")) {
                        Intent it = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(it);
                    }
                    finish();

                }
            }, 5000);
        }
    }
}
