package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.hyphenate.chat.EMClient;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        UIUtils.initStateBar(SplashActivity.this);

        //保证自动登录时可以正确获取到联系人/聊天列表信息
        //EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        
        initView();
    }

    private void initView() {
        ImageView iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_logo.setImageResource(R.drawable.logo);
        // 从浅到深,从百分之10到百分之百
        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        aa.setDuration(3000);//设置动画时间
        iv_logo.setAnimation(aa);//给image设置动画
        aa.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), LoginActivity.class);//logo展示完毕跳转至另一个Activity
                startActivity(intent);
                finish();
            }
        });
    }

}
