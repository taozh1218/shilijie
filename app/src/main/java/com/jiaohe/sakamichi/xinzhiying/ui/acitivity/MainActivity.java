package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.AvatarImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDl_root;
    private AvatarImageView mIv_icon;
    private ImageView mIv_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mDl_root = (DrawerLayout) findViewById(R.id.dl_root);
        mIv_icon = (AvatarImageView) findViewById(R.id.iv_icon);
        mIv_camera = (ImageView) findViewById(R.id.iv_camera);
        () findViewById(R.id.);

        mIv_icon.setOnClickListener(this);
        mIv_camera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_camera:
                
                break;
            case R.id.iv_icon:
                //点击头像弹出侧边栏
                mDl_root.openDrawer(Gravity.LEFT);
                break;
         }
    }
}
