package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.jiaohe.sakamichi.xinzhiying.R;

public class ForgetActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mIb_back;
    private Button mBtn_cert;
    private Button mBtn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        initView();
        initData();
    }

    private void initData() {
        mBtn_cert.setOnClickListener(this);
        mIb_back.setOnClickListener(this);
    }

    private void initView() {
        mIb_back = (ImageButton) findViewById(R.id.ib_back);
        mBtn_cert = (Button) findViewById(R.id.btn_cert);
        mBtn_confirm = (Button) findViewById(R.id.btn_confirm);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_cert:

                break;
            case R.id.btn_confirm:
                break;
        }
    }
}
