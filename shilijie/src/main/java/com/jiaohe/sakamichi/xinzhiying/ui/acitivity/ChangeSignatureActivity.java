package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;

public class ChangeSignatureActivity extends AppCompatActivity {

    private EditText mEdt_signature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_signature);
        init();
    }


    private void init() {
        ImageView img_back = (ImageView) findViewById(R.id.img_back_changeSignAct);
        Button btn_save = (Button) findViewById(R.id.btn_save_changeSignAct);
        mEdt_signature = (EditText) findViewById(R.id.edt_signature_changeSignAct);
        img_back.setOnClickListener(mOnClickListener);
        btn_save.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.img_back_changeSignAct:
                    ChangeSignatureActivity.this.finish();
                    break;
                case R.id.btn_save_changeSignAct:
                    String signature = mEdt_signature.getText().toString();
                    if (TextUtils.isEmpty(signature)) {
                        Toast.makeText(getApplicationContext(), "不能为空！", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    SPUtils.putString(getApplicationContext(), "sign", signature);
                    SPUtils.putBoolean(getApplicationContext(), "isUpload", true);

                    finish();
                    break;
            }
        }
    };

}
