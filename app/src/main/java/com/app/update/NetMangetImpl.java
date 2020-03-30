package com.app.update;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetMangetImpl implements NetManager {
    //endregion
    //</editor-fold>
    private static OkHttpClient client;
    private static Handler handler=new Handler(Looper.getMainLooper());//和UI线程的looper进行绑定
    static {
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        client=builder.build();

        //https自签名的，okhttp握手的错误
       // builder.sslSocketFactory();
    }
    @Override
    public void download(String url, final File file, final NetdownloadCallBack netdownloadCallBack,Object tag) {
        if(!file.exists()){
            file.getParentFile().mkdir();
        }
        Request.Builder builder=new Request.Builder();
        Request request = builder.url(url).get().tag(tag).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        netdownloadCallBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream is=null;
                OutputStream os=null;
                try {
                    final long totalLen=response.body().contentLength();

                    is=response.body().byteStream();
                    os=new FileOutputStream(file);

                    byte[] buffer=new byte[8*1024];
                    long curLen=0;
                    int bufferLen=0;

                    while (!call.isCanceled()&&(bufferLen=is.read(buffer))!=-1){
                        os.write(buffer,0,bufferLen);
                        os.flush();
                        curLen+=bufferLen;
                        final long finalCurLen=curLen;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                netdownloadCallBack.progress((int)( finalCurLen*1.0f/totalLen*100));
                            }
                        });
                    }

                    if(call.isCanceled()){
                        return;
                    }

                    try {
                        file.setExecutable(true,false);
                        file.setReadable(true,false);
                        file.setWritable(true,false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            netdownloadCallBack.success(file);
                        }
                    });
                } catch (final Throwable e) {
                    e.printStackTrace();
                    if(call.isCanceled()){
                        return;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            netdownloadCallBack.failed(e);
                        }
                    });
                }finally {
                    if (is!=null){
                        is.close();
                    }
                    if (os!=null){
                        os.close();
                    }
                }

            }
        });
    }

    public void cancel(Object tag){
        List<Call> queuedCalls = client.dispatcher().queuedCalls();
        if(queuedCalls!=null){
            for (Call call:queuedCalls){
                if(tag.equals(call.request().tag())){
                    call.cancel();
                }
            }
        }

        List<Call> runningCalls = client.dispatcher().runningCalls();
        if(runningCalls!=null){
            for (Call call:runningCalls){
                if(tag.equals(call.request().tag())){
                    call.cancel();
                }
            }
        }
    }

}
