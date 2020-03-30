package com.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends BaseActivity {
    TextView verificationText;
    EditText editText3, editText4, editText5;
    Button button;

    private void initView() {
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        editText5 = findViewById(R.id.editText5);
        button = findViewById(R.id.button2);
        initToolBar(true,"注册");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = editText3.getText().toString();
                if (StringUtil.isBlank(phone)) {
                    Toast.makeText(RegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phone.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String inviteCode = editText4.getText().toString();
                if (StringUtil.isBlank(inviteCode)) {
                    Toast.makeText(RegisterActivity.this, "请输入邀请码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (inviteCode.length() != 11) {
                    Toast.makeText(RegisterActivity.this, "请输入正确的邀请码", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String verCode = editText5.getText().toString();
                if (StringUtil.isBlank(verCode)) {
                    Toast.makeText(RegisterActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sp = getSharedPreferences("system", Context.MODE_PRIVATE);
                String vid = sp.getString("vid", "");
                Map<String, String> body = new HashMap<>();
                body.put("mobile", phone);
                body.put("verifycode", verCode);
                body.put("inviteCd", inviteCode);

                Map<String, String> head = new HashMap<>();
                head.put("vid", SystemUtil.getSharedPreferences(RegisterActivity.this, "system", "vid"));

                String url = ApplicationUtil.getProperties(getApplicationContext(), "url") + "1003";
                OkHttpUtil.getInstance().postJsonAsyn(url, head, body, new OkHttpUtil.NetCall() {
                    @Override
                    public void success(String body) {
                        Map<String, String> res = JsonUtil.fromJson(body, Map.class);
                        if (!res.get("respCd").equals("0000")) {
                            Toast.makeText(RegisterActivity.this, res.get("respMsg"), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent it = getIntent();
                            it.putExtra("phone", editText3.getText().toString());
                            setResult(0x02, it);
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
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
        });
    }

    private int reclen = 60;
    Timer timer;

    public void verification(View view) {
        if (reclen < 60) {
            return;
        }
        final String phone = editText3.getText().toString();
        if (StringUtil.isBlank(phone)) {
            Toast.makeText(RegisterActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 11) {
            Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        verificationText = view.findViewById(R.id.textView7);
        timer = new Timer();
        timer.schedule(new MyTask(), 1000, 1000);

        Map<String, String> body = new HashMap<>();
        body.put("verifycodeTp", "01");
        body.put("mobile", phone);

        Map<String, String> head = new HashMap<>();
        head.put("vid", SystemUtil.getSharedPreferences(RegisterActivity.this, "system", "vid"));

        String url = ApplicationUtil.getProperties(getApplicationContext(), "url") + "1002";
        OkHttpUtil.getInstance().postJsonAsyn(url, head, body, new OkHttpUtil.NetCall() {
            @Override
            public void success(String body) {
                Map<String, String> res = JsonUtil.fromJson(body, Map.class);
                Log.e("Response", res.toString());
                if (!res.get("respCd").equals("0000")) {
                    Toast.makeText(RegisterActivity.this, res.get("respMsg"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed(Call call, IOException e) {
                return;
            }
        });
    }

    public class MyTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (reclen < 10) {
                        verificationText.setText("  0" + reclen + "后重新获取");
                    } else {
                        verificationText.setText(" " + reclen + "后重新获取");
                    }
                    reclen--;
                    if (reclen < 0) {
                        reclen = 60;
                        verificationText.setText("          重新获取");
                        timer.cancel();
                    }
                }
            });
        }
    }


    public void goToLogin(View view) {
        onBackPressed();
    }
}
