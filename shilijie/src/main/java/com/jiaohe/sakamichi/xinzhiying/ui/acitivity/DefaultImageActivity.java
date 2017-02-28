package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;

public class DefaultImageActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mIv_one,mIv_two,mIv_three;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_image);
        UIUtils.initStateBar(this);
        initView();

    }

    private void initView() {
        mIv_one= (ImageView) findViewById(R.id.image_one);
        mIv_two= (ImageView) findViewById(R.id.image_two);
        mIv_three= (ImageView) findViewById(R.id.image_three);
        mIv_one.setOnClickListener(this);
        mIv_two.setOnClickListener(this);
        mIv_three.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_one:
                SPUtils.putString(this,"imageId","1");
                    showPopupWindow();
                break;
            case R.id.image_two:
                SPUtils.putString(this,"imageId","2");

                break;
            case R.id.image_three:
                SPUtils.putString(this,"imageId","3");

                break;
        }
    }

    private void showPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_choose_image, null);
        PopupWindow popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setFocusable(true);

    }


}
