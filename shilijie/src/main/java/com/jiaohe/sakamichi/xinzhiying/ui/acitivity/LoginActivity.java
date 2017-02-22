package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.util.LogUtils;
import com.jiaohe.sakamichi.xinzhiying.util.Md5Utils;
import com.jiaohe.sakamichi.xinzhiying.util.RequestUtils;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;
import com.jiaohe.sakamichi.xinzhiying.util.VolleyInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn_login;
    private TextView mTV_reg;
    private EditText mEt_id;
    private EditText mEt_pw;
    private TextView mTv_forget;

    private String mPhoneNum;
    private String mPassword;
    private String tag = "LoginActivity";
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UIUtils.initStateBar(LoginActivity.this);
        //判断token是否存在
        isLogIn();

        initView();
        initData();
    }

    private void isLogIn() {
        String token = SPUtils.getString(this, "token", null);
        isTokenValid(); //token是否有效
        if (token != null || isTokenValid()) {
            //登录成功跳转到主界面
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean isTokenValid() {
        final boolean[] isValid = {false};
        String body = "phone=" + SPUtils.getString(this, "phone", "");
        RequestUtils.postJsonRequest(ConstantValues.CHECK_TOKEN_URL, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (result.equals("RC100")) {
                        LogUtils.d("令牌有效");
                        isValid[0] = true;
                    } else {
                        LogUtils.d("令牌失效");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
            }
        });
        return isValid[0];
    }

    private void initData() {
        mBtn_login.setOnClickListener(this);
        mTV_reg.setOnClickListener(this);
        mTv_forget.setOnClickListener(this);
    }

    private void initView() {
        mBtn_login = (Button) findViewById(R.id.btn_login);
        mTV_reg = (TextView) findViewById(R.id.tv_reg);
        mEt_id = (EditText) findViewById(R.id.et_id);
        mEt_pw = (EditText) findViewById(R.id.et_pw);
        mTv_forget = (TextView) findViewById(R.id.tv_forget);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                mDialog = ProgressDialog.show(this, "",
                        "正在登录...", true, true);
                Login();
                break;
            case R.id.tv_reg:
                //跳转注册界面
                Intent intent_reg = new Intent(this, RegActivity.class);
                startActivity(intent_reg);
                break;
            case R.id.tv_forget:
                //跳转找回密码
                Intent intent_forget = new Intent(this, ForgetActivity.class);
                startActivity(intent_forget);
                break;
        }
    }

    private void Login() {
        mPhoneNum = mEt_id.getText().toString().trim();
        mPassword = mEt_pw.getText().toString().trim();
        LogUtils.d(mPhoneNum + "=========" + mPassword);
        if (TextUtils.isEmpty(mPhoneNum) || TextUtils.isEmpty(mPassword)) { //用户名或密码为空
            Toast.makeText(this, "请将信息填写完全", Toast.LENGTH_SHORT).show();
        } else {
            requestServer();
        }
    }

    private void requestServer() {
        String body = "phone=" + mPhoneNum + "&pass=" + Md5Utils.encode(mPassword);
        RequestUtils.postJsonRequest(ConstantValues.LOGIN_URL, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (result.equals("RC100")) {
                        LogUtils.d("登录成功");
                        //如果本地无缓存，则缓存返回的token和手机号码
                        SPUtils.putString(LoginActivity.this, "phone", mPhoneNum);
                        SPUtils.putString(LoginActivity.this, "token", response.getString("token"));
                        //关闭进度条对话框
                        mDialog.dismiss();
                        //登录成功跳转到主界面
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        LogUtils.d("登录失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                VolleyLog.d(tag, error.getMessage());
            }
        });
    }
}
