package com.app.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Toast;

import com.app.R;
import com.app.nfc.UnionpayTag;

public class NFCActivity extends BaseActivity {
    NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        initToolBar(true,"当面付");

    }

    @Override
    protected void onStart() {
        super.onStart();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(NFCActivity.this, "设备不支持NFC！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (nfcAdapter != null && !nfcAdapter.isEnabled()) {
            Toast.makeText(NFCActivity.this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 5. 重写OnIntent方法, 读取到的数据存储在intent中
        // 获取到意图中的Tag数据
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Toast.makeText(NFCActivity.this, UnionpayTag.readTagData(tag), Toast.LENGTH_SHORT).show();
    }
}
