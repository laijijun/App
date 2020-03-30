package com.app.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.app.R;
import com.app.util.ApplicationUtil;
import com.app.util.ByteUtil;
import com.app.util.FileUtil;
import com.app.util.JsonUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImgUploadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_upload);
        initToolBar(true,"认证");
    }

    private Uri imageUri;
    String[] strs = new String[]{"拍照", "从相册选择"};
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int CHOOSE_PHOTO = 3;
    private LinearLayout linearLayout;
    private TextView textView;
    ProgressDialog progressDialog;

    private View view;
    public void choose(View view) {
        this.view=view;
        AlertDialog.Builder builder = new AlertDialog.Builder(ImgUploadActivity.this);
        //列表对话框
        builder.setItems(strs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            imageUri = FileProvider.getUriForFile(ImgUploadActivity.this,"com.app.provider", outputImage);
                        } else {
                            imageUri = Uri.fromFile(outputImage);
                        }
                        //启动相机程序
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, TAKE_PHOTO);
                        break;
                    case 1:
                        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
                        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intentToPickPic, CHOOSE_PHOTO);
                        break;
                }
            }
        });
        builder.show();
    }

    Map<String,String> photos=new HashMap<>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO);//启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Drawable btnDrawable = new BitmapDrawable(bitmap);
                        switch (view.getId()) {
                            case R.id.front:
                                linearLayout = view.findViewById(R.id.front);
                                textView=view.findViewById(R.id.textView13);
                                photos.put("01",ByteUtil.bitmapToBase64(bitmap));
                                break;
                            case R.id.behind:
                                linearLayout = view.findViewById(R.id.behind);
                                textView=view.findViewById(R.id.textView15);
                                photos.put("02",ByteUtil.bitmapToBase64(bitmap));
                                break;
                        }
                        linearLayout.setBackground(btnDrawable);
                        textView.setText("");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String filePath = FileUtil.getFilePathByUri(this, uri);
                    if (!TextUtils.isEmpty(filePath)) {
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        Drawable btnDrawable = new BitmapDrawable(bitmap);
                        switch (view.getId()) {
                            case R.id.front:
                                linearLayout = view.findViewById(R.id.front);
                                textView = view.findViewById(R.id.textView13);
                                photos.put("01",ByteUtil.bitmapToBase64(bitmap));
                                break;
                            case R.id.behind:
                                linearLayout = view.findViewById(R.id.behind);
                                textView = view.findViewById(R.id.textView15);
                                photos.put("02",ByteUtil.bitmapToBase64(bitmap));
                                break;
                        }
                        linearLayout.setBackground(btnDrawable);
                        textView.setText("");
                    }
                }
                break;
        }
    }

    private OkHttpClient client;
    private  String vid;
    private String ssoid;
    MediaType contentType ;

    public void submit(View view) {
        if(TextUtils.isEmpty(photos.get("01"))){
            Toast.makeText(ImgUploadActivity.this, "请选择身份证正面照", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(photos.get("02"))){
            Toast.makeText(ImgUploadActivity.this, "请选择身份证反面照", Toast.LENGTH_SHORT).show();
            return;
        }
        //显示进度条
        progressDialog=new ProgressDialog(ImgUploadActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();


        SharedPreferences sp = getSharedPreferences("system", Context.MODE_PRIVATE);
        vid = sp.getString("vid", "");
        Map<String, String> head = new HashMap<>();
        head.put("vid", vid);
        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        ssoid = userInfo.getString("ssoid", "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new OkHttpClient();
                contentType = MediaType.parse("application/json; charset=utf-8");
                Map<String, String> body = new HashMap<>();
                body.put("ssoid", ssoid);

                //文件上传
                for (String key : photos.keySet()) {
                    body.put("fileType", key);
                    body.put("file", photos.get(key).replaceAll("\n", "").replaceAll(" ", ""));
                    Request.Builder builder = new Request.Builder();
                    builder.url(ApplicationUtil.getProperties(getApplicationContext(), "url") + "1006");
                    builder.addHeader("vid", vid);
                    builder.post(RequestBody.create(contentType, JsonUtil.toJson(body)));
                    Request request = builder.build();
                    try {
                       client.newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //提交审核
                body.remove("fileType");
                body.remove("file");
                Request.Builder builder = new Request.Builder();
                builder.url(ApplicationUtil.getProperties(getApplicationContext(), "url") + "1007");
                builder.addHeader("vid", vid);
                builder.post(RequestBody.create(contentType, JsonUtil.toJson(body)));
                Request request = builder.build();
                try {
                    Response response = client.newCall(request).execute();
                    String res = response.body().string();
                    showRequest(res);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void showRequest(final String request) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> body = new HashMap<>();
                body.put("ssoid", ssoid);
                Request.Builder builder = new Request.Builder();
                builder.url(ApplicationUtil.getProperties(getApplicationContext(), "url") + "1008");
                builder.addHeader("vid", vid);
                builder.post(RequestBody.create(contentType, JsonUtil.toJson(body)));
                Request request = builder.build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(ImgUploadActivity.this, "用户信息查询失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                         Map<String,String> userInfo=JsonUtil.fromJson(response.body().string(),Map.class);
                        SharedPreferences sp=getSharedPreferences("userInfo",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("mobile",userInfo.get("mobile"));
                        editor.putString("state",userInfo.get("state"));
                        editor.putString("name",userInfo.get("name"));
                        editor.putString("certNo",userInfo.get("certNo"));
                        editor.putString("merId",userInfo.get("merId"));
                        editor.putString("merName",userInfo.get("merName"));
                        editor.putString("inviteCd",userInfo.get("inviteCd"));
                        editor.commit();

                        Intent it=getIntent();
                        it.putExtra("state", userInfo.get("state"));
                        setResult(0x02,it);
                        //关闭进度条
                        progressDialog.cancel();
                        finish();
                    }
                });
            }
        });
    }
}
