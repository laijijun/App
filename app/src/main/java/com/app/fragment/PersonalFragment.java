package com.app.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.app.R;
import com.app.activity.ImgUploadActivity;
import com.app.activity.LoginActivity;
import com.app.activity.NoticeActivity;
import com.app.update.UpdateVersionDialog;
import com.app.util.SystemUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalFragment extends Fragment {
    SimpleAdapter adapter;
    String[] title1 = new String[]{"基本信息", "通知公告", "我的费率", "结算卡","邀请码","客服电话","检查更新"};

    int[] imgs1 = new int[]{R.mipmap.user_ico1, R.mipmap.user_ico2, R.mipmap.user_ico3, R.mipmap.user_ico4,R.mipmap.user_ico5, R.mipmap.user_ico6,R.mipmap.user_ico7};

    int arrow=R.drawable.ic_menu_send;
    private TextView user_name_tv,phone_tv,user_status_tv;
    FragmentActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.personal,container,false);
        activity = this.getActivity();
        final SharedPreferences userInfo = activity.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        user_name_tv=view.findViewById(R.id.textView6);
        phone_tv=view.findViewById(R.id.textView11);
        user_status_tv=view.findViewById(R.id.textView12);
        user_name_tv.setText(userInfo.getString("name",""));
        phone_tv.setText(userInfo.getString("mobile",""));
        setState(userInfo.getString("state",""));

        //功能块一
        ListView listView=view.findViewById(R.id.listView1);
        List<Map<String,Object>> list=new ArrayList<>();
        for (int i=0;i<title1.length;i++){
            Map<String,Object> m=new HashMap<>();
            m.put("name",title1[i]);
            m.put("img",imgs1[i]);
            m.put("arrow",arrow);
            list.add(m);
        }
        adapter = new SimpleAdapter(activity,list,R.layout.personal_item1,new String[]{"name","img","arrow"},new int[]{R.id.textView14,R.id.imageView15,R.id.imageView16});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(activity,"选择了："+title1[position],Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent it=new Intent(activity, NoticeActivity.class);
                        startActivity(it);
                        break;
                    case 2:
                        Toast.makeText(activity,"选择了："+title1[position],Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(activity,"选择了："+title1[position],Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        AlertDialog.Builder builder_yqm = new AlertDialog.Builder(activity);
                        builder_yqm.setTitle("邀请码");
                        builder_yqm.setMessage(userInfo.getString("mobile", ""));
                        builder_yqm.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder_yqm.show();
                        break;
                    case 5:
                        Intent it_phone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:10086"));
                        startActivity(it_phone);
                        break;
                    case 6:
                        SharedPreferences preferences = activity.getSharedPreferences("system", Context.MODE_PRIVATE);
                        String appVersion = preferences.getString("appVersion", "");
                        //检查更新
                        if (Double.parseDouble(appVersion) > SystemUtil.getAppVersion(activity)) {
                            File file=new File(activity.getCacheDir(),"ysf.apk");
                            String sys_md5="14480fc08932105d55b9217c6d2fb90b";
                            //检查MD5
                            String md5=SystemUtil.getFileMd5(file);
                            if(!TextUtils.isEmpty(md5)&&sys_md5.equals(md5)){
                                //安装
                                SystemUtil.installApk(activity, file);
                            }else{
                                UpdateVersionDialog.show(activity,appVersion);
                            }
                        }else{
                            Toast.makeText(activity,"已是最新版本",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            }
        });

        user_status_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(activity, ImgUploadActivity.class);
                startActivityForResult(it, 0x01);
            }
        });



        //退出登录模块
        Button quitBtn=view.findViewById(R.id.button3);
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userInfo!=null){
                    userInfo.edit().clear().commit();
                    Intent it=new Intent(activity, LoginActivity.class);
                    startActivity(it);
                    activity.finish();
                }
            }
        });
        return view;
    }

    private void setState(String user_state){
        if("0".equals(user_state)){
            user_state="已注册(点击认证)";
        }else if("1".equals(user_state)){
            user_state="审核中";
        }else if("2".equals(user_state)){
            user_state="审核通过";
        }else if("3".equals(user_state)){
            user_state="审核未通过(点击重新认证)";
        }else if("4".equals(user_state)){
            user_state="冻结";
        }
        user_status_tv.setText(user_state);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0x01&&resultCode==0x02){
            String user_state=data.getStringExtra("state");
            setState(user_state);
        }
    }
}
