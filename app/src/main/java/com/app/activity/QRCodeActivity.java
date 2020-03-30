package com.app.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.app.R;
import com.yzq.zxinglibrary.encode.CodeCreator;

public class QRCodeActivity extends BaseActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        initToolBar(true,"向商家付款");
        imageView=findViewById(R.id.imageView18);
        Bitmap qrCoce=CodeCreator.createQRCode("http://zf3wpw.natappfree.cc/app-gw/api/qrCode",300,300, BitmapFactory.decodeResource(getResources(),R.mipmap.logo));
        imageView.setImageBitmap(qrCoce);
    }
}
