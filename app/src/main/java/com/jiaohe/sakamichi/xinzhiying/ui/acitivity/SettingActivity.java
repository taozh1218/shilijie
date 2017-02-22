package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        UIUtils.initStateBar(SettingActivity.this);
        initView();
    }

    private void initView() {
        ImageButton ib_back = (ImageButton) findViewById(R.id.ib_back);
        TextView tv_nickname = (TextView) findViewById(R.id.tv_nickname);

        ib_back.setOnClickListener(this);
        tv_nickname.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.tv_nickname:
                intent = new Intent(this, ChangeNameActivity.class);
                startActivity(intent);
                break;
        }
    }
}
