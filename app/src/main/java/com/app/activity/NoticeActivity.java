package com.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.adapter.RecyclerViewAdapter;
import com.app.util.ApplicationUtil;
import com.app.util.JsonUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NoticeActivity extends BaseActivity {
    RecyclerViewAdapter adapter;

    private RefreshLayout mRefreshLayout;

    private RecyclerView recyclerView;

    Map<String, Object> res;
    List<Map<String,Object>> datas = new ArrayList<>();
    int pageNmn=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        initToolBar(true,"通知公告");

        mRefreshLayout = findViewById(R.id.refreshLayout);
        recyclerView =findViewById(R.id.recycler_view);
        //设置LayoutManager为LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(NoticeActivity.this));
        adapter=new RecyclerViewAdapter(NoticeActivity.this);
        recyclerView.setAdapter(adapter);

        send("1","10");

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mRefreshLayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
            }
        });



        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() { //上拉加载更多
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(1000/*,false*/);//传入false表示加载失败
                pageNmn++;
                send(String.valueOf(pageNmn),"10");

            }
        });
    }

    private List<Map<String,String>> send(final String pageNum,final String pageSize){
        SharedPreferences preferences = getSharedPreferences("system", Context.MODE_PRIVATE);
        final String vid = preferences.getString("vid", "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                MediaType contentType = MediaType.parse("application/json; charset=utf-8");
                Map<String, String> body = new HashMap<>();
                body.put("pageNum", pageNum);
                body.put("pageSize", pageSize);

                Request.Builder builder = new Request.Builder();
                builder.url(ApplicationUtil.getProperties(getApplicationContext(),"url")+"1018");
                builder.addHeader("vid", vid);
                builder.post(RequestBody.create(contentType, JsonUtil.toJson(body)));
                Request req = builder.build();
                Response response = null;
                try {
                    response = client.newCall(req).execute();
                    String res = response.body().string();
                    showRequest(res);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return null;
    }

    public void showRequest(final String request) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                res = JsonUtil.fromJson(request, Map.class);
                List<Map<String,Object>> msgInfo=(List<Map<String,Object>>)res.get("msgInfo");
                List<Map<String,Object>> lists = new ArrayList<>();
                for (Map<String,Object> info:msgInfo){

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    Double recUpdTs=(Double) info.get("recUpdTs");
                    BigDecimal date=new BigDecimal(recUpdTs);
                    info.put("recUpdTs",simpleDateFormat.format(new Date(Long.valueOf(date.toPlainString()))));
                    datas.add(info);
                    lists.add(info);
                }
                adapter.addMoreItem(lists);
                adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent it= new Intent();
                        it.setClass(NoticeActivity.this, ScanActivity.class);
                        //activitcy传递数据
                        it.putExtra("url",datas.get(position).get("msgLink").toString());
                        startActivity(it);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                });
                Log.e("返回结果：",res.toString());
            }
        });
    }
}
