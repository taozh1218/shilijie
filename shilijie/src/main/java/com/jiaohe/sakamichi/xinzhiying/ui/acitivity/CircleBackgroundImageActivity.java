package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.CircleActivtyFactory;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;

public class CircleBackgroundImageActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBt_photographic,mBt_photoAlbum,mBt_defaultImage;
    private ImageButton mIb_backCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cricle_background_image);
        CircleActivtyFactory.getAppManager().addActivity(this);
        UIUtils.initStateBar(this);
        initView();
    }

    private void initView() {
        mIb_backCircle= (ImageButton) findViewById(R.id.ib_backCircle);
        mBt_defaultImage= (Button) findViewById(R.id.button_defaultImage);
        mBt_defaultImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_defaultImage:
                Intent intent = new Intent(CircleBackgroundImageActivity.this,DefaultImageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
