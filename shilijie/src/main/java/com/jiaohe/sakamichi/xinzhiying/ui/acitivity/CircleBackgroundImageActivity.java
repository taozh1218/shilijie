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

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_CROP_IMAGE = 2;
    private static final int RESULT_CAMERA_IMAGE = 3;
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
        mBt_photographic= (Button) findViewById(R.id.button_photographic);
        mBt_photographic.setOnClickListener(this);
        mBt_photoAlbum= (Button) findViewById(R.id.button_photoAlbum);
        mBt_photoAlbum.setOnClickListener(this);
        mIb_backCircle= (ImageButton) findViewById(R.id.ib_backCircle1);
        mIb_backCircle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_defaultImage:
                Intent intent = new Intent(CircleBackgroundImageActivity.this,DefaultImageActivity.class);
                startActivity(intent);
                break;
            case R.id.ib_backCircle1:

            break;
            case R.id.button_photoAlbum:
                //pickImageFromAlbum();
            break;
            case R.id.button_photographic:

            break;


        }
    }




}
