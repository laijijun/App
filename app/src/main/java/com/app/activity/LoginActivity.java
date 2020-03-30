package com.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.R;
import com.app.util.ApplicationUtil;
import com.app.util.JsonUtil;
import com.app.util.OkHttpUtil;
import com.app.util.SystemUtil;
import com.blankj.utilcode.util.RegexUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

public class LoginActivity extends BaseActivity {
    private EditText phoneEdit,verCdEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phoneEdit = findViewById(R.id.editText);
        verCdEdit=findViewById(R.id.editText2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0x01 && resultCode == 0x02) {
            phoneEdit.setText(data.getStringExtra("phone"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private int reclen = 60;
    Timer timer;
    TextView verificationText;

    /**
     * 获取验证码
     * @param view
     */
    public void verification(View view) {
        if(reclen<60){
            return;
        }
        final String phone = phoneEdit.getText().toString();
        if (!RegexUtils.isMobileExact(phone)) {
            Toast.makeText(LoginActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        verificationText=view.findViewById(R.id.textView8);
        timer = new Timer();
        timer.schedule(new MyTask(), 1000, 1000);

        Map<String, String> body = new HashMap<>();
        body.put("verifycodeTp", "02");
        body.put("mobile", phone);

        Map<String, String> head = new HashMap<>();
        head.put("vid", SystemUtil.getSharedPreferences(LoginActivity.this,"system","vid"));

        String url=ApplicationUtil.getProperties(getApplicationContext(),"url")+"1002";
        OkHttpUtil.getInstance().postJsonAsyn(url, head, body, new OkHttpUtil.NetCall() {
            @Override
            public void success(String body) {
                Map<String, String> res = JsonUtil.fromJson(body, Map.class);
                if (!res.get("respCd").equals("0000")) {
                    Toast.makeText(LoginActivity.this, res.get("respMsg"), Toast.LENGTH_SHORT).show();
                }
                Log.e("Response", res.toString());
            }

            @Override
            public void failed(Call call, IOException e) {
                return;
            }
        });
    }

    /**
     * 验证码倒计时
     */
    public class  MyTask extends TimerTask{
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(reclen<10){
                        verificationText.setText("  0"+reclen + "后重新获取");
                    }else{
                        verificationText.setText(" "+reclen + "后重新获取");
                    }
                    reclen--;
                    if (reclen < 0) {
                        reclen=60;
                        verificationText.setText("          重新获取");
                        timer.cancel();
                    }
                }
            });
        }
    }

    /**
     * 登录
     * @param view
     */
    public void login(View view) {
        final String phone = phoneEdit.getText().toString();
        if (!RegexUtils.isMobileExact(phone)) {
            Toast.makeText(LoginActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        final String verCd = verCdEdit.getText().toString();
        if (TextUtils.isEmpty(verCd)) {
            Toast.makeText(LoginActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("mobile", phone);
        body.put("verifycode", verCd);

        Map<String, String> head = new HashMap<>();
        head.put("vid", SystemUtil.getSharedPreferences(LoginActivity.this,"system","vid"));

        String url=ApplicationUtil.getProperties(getApplicationContext(),"url")+"1004";

        OkHttpUtil.getInstance().postJsonAsyn(url, head, body, new OkHttpUtil.NetCall() {
            @Override
            public void success(String body) {
                Map<String, String> res = JsonUtil.fromJson(body, Map.class);
                if (!res.get("respCd").equals("0000")) {
                    Toast.makeText(LoginActivity.this, res.get("respMsg"), Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences sp=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("ssoid",res.get("ssoid"));
                    Map<String,String> userInfo=JsonUtil.fromJson(JsonUtil.toJson(res.get("userInfo")),Map.class);
                    editor.putString("mobile",userInfo.get("mobile"));
                    editor.putString("state",userInfo.get("state"));
                    editor.putString("name",userInfo.get("name"));
                    editor.putString("certNo",userInfo.get("certNo"));
                    editor.putString("merId",userInfo.get("merId"));
                    editor.putString("merName",userInfo.get("merName"));
                    editor.putString("inviteCd",userInfo.get("inviteCd"));
                    editor.commit();
                    Intent it = new Intent(LoginActivity.this, IndexActivity.class);
                    startActivity(it);
                    finish();
                }
                Log.e("Response", res.toString());
            }

            @Override
            public void failed(Call call, IOException e) {
                return;
            }
        });
    }

    /**
     * 跳转到登录页面
     * @param view
     */
    public void goToRregister(View view){
        Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(it,0x01);
    }

}
