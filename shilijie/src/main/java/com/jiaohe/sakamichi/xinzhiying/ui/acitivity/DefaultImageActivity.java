package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.CircleActivtyFactory;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;

public class DefaultImageActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mIv_one,mIv_two,mIv_three;
    private PopupWindow popupWindow;
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
                showPopupWindow(1);
                break;
            case R.id.image_two:
                SPUtils.putString(this,"imageId","2");
                showPopupWindow(2);
                break;
            case R.id.image_three:
                SPUtils.putString(this,"imageId","3");
                showPopupWindow(3);
                break;
            case R.id.button_close:
                popupWindow.dismiss();
                break;
            case R.id.button_sure:
                finish();
                CircleActivtyFactory.getAppManager().finishAllActivity();
                break;
        }
    }

    private void showPopupWindow(int i) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_choose_image, null);
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setFocusable(true);
        Button btu_close = (Button) view.findViewById(R.id.button_close);
        Button btu_sure= (Button) view.findViewById(R.id.button_sure);
        btu_close.setOnClickListener(this);
        btu_sure.setOnClickListener(this);

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        switch (i){
            case 1:
                popupWindow.showAtLocation(mIv_one, Gravity.CENTER|Gravity.TOP,0,height/4);
                break;
            case 2:
                popupWindow.showAtLocation(mIv_two, Gravity.CENTER|Gravity.TOP,0,height/2);
                break;
            case 3:
                popupWindow.showAtLocation(mIv_three, Gravity.CENTER|Gravity.TOP,0,height*8/10);
                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        popupWindow.dismiss();
    }
}
