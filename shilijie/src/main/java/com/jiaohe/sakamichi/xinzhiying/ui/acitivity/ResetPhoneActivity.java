package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.ui.view.CountButton;
import com.jiaohe.sakamichi.xinzhiying.util.RegexUtils;
import com.jiaohe.sakamichi.xinzhiying.util.RequestUtils;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;
import com.jiaohe.sakamichi.xinzhiying.util.VolleyInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    private String tag ="ResetPhoneActivity";
    private String oPhone,token,newPhone,code;
    private EditText mEt_newPhone,mEt_phoneCode;
    private ImageButton mIb_backSetting;
    private Button resetPhone;
    private CountButton getPhoneCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_phone);
        UIUtils.initStateBar(ResetPhoneActivity.this);
        initView();
    }

    private void initView() {
        oPhone= SPUtils.getString(this,"phone","");
        token=SPUtils.getString(this,"token","");
        mEt_newPhone= (EditText) findViewById(R.id.et_newPhone);
        mEt_phoneCode= (EditText) findViewById(R.id.et_certPhoneCode);
        getPhoneCode= (CountButton) findViewById(R.id.bt_certPhone);
        resetPhone= (Button) findViewById(R.id.bt_resetPhone);
        mIb_backSetting= (ImageButton) findViewById(R.id.ib_backSetting);
        mIb_backSetting.setOnClickListener(this);
        getPhoneCode.setOnClickListener(this);
        resetPhone.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_certPhone:
                newPhone=mEt_newPhone.getText().toString().trim();
                if (TextUtils.isEmpty(newPhone)||!RegexUtils.checkNum(newPhone)){
                    Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                }else{
                    getCode("100");
                }
                break;
            case R.id.bt_resetPhone:
                code=mEt_phoneCode.getText().toString().trim();
                if (TextUtils.isEmpty(newPhone)||TextUtils.isEmpty(code)||code.length()!=6){
                    Toast.makeText(this, "请填写正确的信息", Toast.LENGTH_SHORT).show();
                }else {
                    //验证新手机是否已经注册
                    verifyPhone();
                }
                break;
            case R.id.ib_backSetting:
                finish();
                break;
        }
    }

    private void verifyPhone() {
        String body = "phone="+newPhone;
        RequestUtils.postJsonRequest(ConstantValues.IS_USER, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (result.equals("RC100")){
                        resetPhone();
                    }else  if (result.equals("RC200")){
                        Toast.makeText(getApplicationContext(), "此手机号已经注册", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "操作异常", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error) {
                VolleyLog.d(tag, "获取失败 请重新获取验证码");
            }
        });
    }



    private void resetPhone() {
        String body = "ophone=" + oPhone + "&cphone=" + newPhone+"&token="+token+"&code="+code;
        RequestUtils.postJsonRequest(ConstantValues.USER_RESET_PHONE, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (result.equals("RC100")){
                        Toast.makeText(getApplicationContext(), "绑定新手机成功", Toast.LENGTH_SHORT).show();
                        SPUtils.putString(getApplicationContext(),"phone",newPhone);
                        finish();
                    }else  if (result.equals("RC200")){
                        Toast.makeText(getApplicationContext(), "绑定新手机操作失败", Toast.LENGTH_SHORT).show();
                    }else if (result.equals("RC213")){
                        Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_SHORT).show();
                    }else if (result.equals("RC404")){
                        Toast.makeText(getApplicationContext(), "验证码失效", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(VolleyError error) {
                VolleyLog.d(tag, "获取失败 请重新获取验证码");
            }
        });




    }

    public void getCode(String type) {
        String body = "phone=" + newPhone + "&type=" + type;
        RequestUtils.postJsonRequest(ConstantValues.CERT_URL, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
            }

            @Override
            public void onError(VolleyError error) {
                VolleyLog.d(tag, "获取失败 请重新获取验证码");
            }
        });

    }
}
