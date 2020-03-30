package com.app.update;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.app.R;
import com.app.util.SystemUtil;

import java.io.File;

public class UpdateVersionDialog extends DialogFragment {
    private static final String DOWNLOAD_KEY="download_key";

    private String version;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments!=null){
            version=arguments.getString(DOWNLOAD_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.app_update_dialog,container,false);
            bindEvents(view);
            return view;
    }
    private void bindEvents(View view){
        TextView dialog_title= view.findViewById(R.id.dialog_title);
        TextView dialog_content= view.findViewById(R.id.dialog_content);
        final TextView dialog_upd_button= view.findViewById(R.id.dialog_upd_button);
        final TextView dialog_cancel_button= view.findViewById(R.id.dialog_cancel_button);
        dialog_title.setText("版本更新");
        dialog_content.setText(version+"版本更新啦！");
        dialog_upd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog_cancel_button.setVisibility(View.GONE);
                dialog_upd_button.setWidth(600);
                v.setEnabled(false);
                String url="http://59.110.162.30/v450_imooc_updater.apk";
                final File file=new File(getActivity().getCacheDir(),"ysf.apk");
                AppUpdate.getInstance().getNetManager().download(url, file, new NetdownloadCallBack() {
                    @Override
                    public void success(File apkFile) {
                        v.setEnabled(true);
                        String sys_md5="14480fc08932105d55b9217c6d2fb90b";
                        dismiss();
                        //检查MD5
                        String md5=SystemUtil.getFileMd5(file);
                        if(!TextUtils.isEmpty(md5)&&sys_md5.equals(md5)){
                            //安装
                            SystemUtil.installApk(getActivity(), file);
                        }
                    }

                    @Override
                    public void progress(int progress) {
                        //更新界面的代码
                        dialog_upd_button.setText(progress+"%");
                        dialog_upd_button.setTextColor(Color.WHITE);
                    }
                    @Override
                    public void failed(Throwable throwable) {
                        v.setEnabled(true);
                        Toast.makeText(getActivity(),"文件下载失败",Toast.LENGTH_SHORT).show();
                    }
                },UpdateVersionDialog.this);
            }
        });


        dialog_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.e("==========","onDismiss");
        AppUpdate.getInstance().getNetManager().cancel(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public  static void show(FragmentActivity activity, String version){
        Bundle bundle=new Bundle();
        bundle.putString(DOWNLOAD_KEY,version);
        UpdateVersionDialog dialog=new UpdateVersionDialog();
        dialog.setArguments(bundle);
        dialog.setCancelable(false);
        dialog.show(activity.getSupportFragmentManager(),"updateVersionDialog");
    }
}
