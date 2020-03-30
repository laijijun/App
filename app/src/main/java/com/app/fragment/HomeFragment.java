package com.app.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.app.R;
import com.app.activity.MapActivity;
import com.app.activity.NFCActivity;
import com.app.activity.QRCodeActivity;
import com.app.activity.ScanActivity;
import com.app.adapter.LoopViewAdapter;
import com.app.listener.PagerOnClickListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    GridView gridView, gridView1;
    private int REQUEST_CODE_SCAN = 111;
    public static final int RESULT_OK = -1;
    FragmentActivity activity=null;

    private ViewPager viewPager;  //轮播图模块
    private int[] mImg;
    private int[] mImg_id;
    private String[] mDec;
    private ArrayList<ImageView> mImgList;
    private LinearLayout ll_dots_container;
    private TextView loop_dec;
    private int previousSelectedPosition = 0;
    boolean isRunning = false;


    String[] title = new String[]{"收支明细", "财务查询", "到账卡管理", "常用卡管理", "我的费率","导航"};
    int[] img = new int[]{R.mipmap.index_s1, R.mipmap.index_s2, R.mipmap.index_s3, R.mipmap.index_s4, R.mipmap.index_s5,R.mipmap.index_s6};
    String[] topTitle = new String[]{"当面付", "扫一扫", "付款码"};
    int[] topImg = new int[]{R.mipmap.ico1, R.mipmap.ico2, R.mipmap.ico3};



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = this.getActivity();
        View view = inflater.inflate(R.layout.home, container, false);


        initLoopView(view);

        gridView = view.findViewById(R.id.gridView);
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", title[i]);
            m.put("img", img[i]);
            list.add(m);
        }
        SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), list, R.layout.home_item, new String[]{"name", "img"}, new int[]{R.id.textView5, R.id.imageView10});
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        Intent it = new Intent(activity, MapActivity.class);
                        startActivity(it);
                        break;
                }
            }
        });

        gridView1 = view.findViewById(R.id.gridView1);
        list = new ArrayList<>();
        for (int i = 0; i < topTitle.length; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("name", topTitle[i]);
            m.put("img", topImg[i]);
            list.add(m);
        }

        SimpleAdapter adapter1 = new SimpleAdapter(this.getActivity(), list, R.layout.home_item, new String[]{"name", "img"}, new int[]{R.id.textView5, R.id.imageView10});
        gridView1.setAdapter(adapter1);
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent it = new Intent(activity, NFCActivity.class);
                        startActivity(it);
                    break;
                    case 1:
                        AndPermission.with(activity).permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE).onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Intent intent = new Intent(activity, CaptureActivity.class);
                                /*ZxingConfig是配置类
                                 *可以设置是否显示底部布局，闪光灯，相册，
                                 * 是否播放提示音  震动
                                 * 设置扫描框颜色等
                                 * 也可以不传这个参数
                                 * */
                                ZxingConfig config = new ZxingConfig();
                                config.setPlayBeep(false);//是否播放扫描声音 默认为true
                                //  config.setShake(false);//是否震动  默认为true
                                //config.setDecodeBarCode(false);//是否扫描条形码 默认为true
//                                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
//                                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
//                                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                startActivityForResult(intent, REQUEST_CODE_SCAN);
                            }
                        }).onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Uri packageURI = Uri.parse("package:" +activity.getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

                                Toast.makeText(activity, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                            }
                        }).start();
                        break;
                    case 2:
                        Intent qrIt=new Intent(activity, QRCodeActivity.class);
                        startActivity(qrIt);
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                /*Toast.makeText(this.getActivity(),"扫描结果为：" + content,Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse(content);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);*/

                Intent it= new Intent();
                it.setClass(activity, ScanActivity.class);
                //activitcy传递数据
                it.putExtra("url",content);
                startActivity(it);

            }
        }
    }

    private void initLoopView(View view) {
        viewPager = view.findViewById(R.id.loopviewpager);
        ll_dots_container = view.findViewById(R.id.ll_dots_loop);
        loop_dec = view.findViewById(R.id.loop_dec);

        // 图片资源id数组
        mImg = new int[]{
                R.mipmap.ad,
                R.mipmap.ad,
                R.mipmap.ad,
                R.mipmap.ad,
                R.mipmap.ad
        };

        // 文本描述
        mDec = new String[]{
                "Test1",
                "Test2",
                "Test3",
                "Test4",
                "Test5"
        };

        mImg_id = new int[]{
                R.id.pager_img1,
                R.id.pager_img2,
                R.id.pager_img3,
                R.id.pager_img4,
                R.id.pager_img5
        };

        // 初始化要展示的5个ImageView
        mImgList = new ArrayList<ImageView>();
        ImageView imageView;
        View dotView;
        LinearLayout.LayoutParams layoutParams;
        for(int i=0;i<mImg.length;i++){
            //初始化要显示的图片对象
            imageView = new ImageView(activity);
            imageView.setBackgroundResource(mImg[i]);
            imageView.setId(mImg_id[i]);
            imageView.setOnClickListener(new PagerOnClickListener(activity.getApplicationContext()));
            mImgList.add(imageView);
            //加引导点
            dotView = new View(activity);
            dotView.setBackgroundResource(R.drawable.dot);
            layoutParams = new LinearLayout.LayoutParams(10,10);
            if(i!=0){
                layoutParams.leftMargin=10;
            }
            //设置默认所有都不可用
            dotView.setEnabled(false);
            ll_dots_container.addView(dotView,layoutParams);
        }

        ll_dots_container.getChildAt(0).setEnabled(true);
        loop_dec.setText(mDec[0]);
        previousSelectedPosition=0;
        //设置适配器
        viewPager.setAdapter(new LoopViewAdapter(mImgList));
        // 把ViewPager设置为默认选中Integer.MAX_VALUE / t2，从十几亿次开始轮播图片，达到无限循环目的;
        int m = (Integer.MAX_VALUE / 2) %mImgList.size();
        int currentPosition = Integer.MAX_VALUE / 2 - m;
        viewPager.setCurrentItem(currentPosition);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                int newPosition = i % mImgList.size();
                loop_dec.setText(mDec[newPosition]);
                ll_dots_container.getChildAt(previousSelectedPosition).setEnabled(false);
                ll_dots_container.getChildAt(newPosition).setEnabled(true);
                previousSelectedPosition = newPosition;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // 开启轮询
        new Thread(){
            public void run(){
                isRunning = true;
                while(isRunning){
                    try{
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //下一条
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                        }
                    });
                }
            }
        }.start();

    }
}
