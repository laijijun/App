package com.app.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.R;

public class BaseActivity extends AppCompatActivity {
    private ImageView toolbar_back;
    private TextView toolbar_title;

    protected  void initToolBar(boolean isShowBack,String title){
        toolbar_back=findViewById(R.id.toolbar_back);
        toolbar_title=findViewById(R.id.toolbar_title);
        toolbar_back.setVisibility(isShowBack? View.VISIBLE:View.GONE);
        toolbar_title.setText(title);
        toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
