package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.utils.LogUtils;
import com.jiaohe.sakamichi.xinzhiying.utils.Md5Utils;
import com.jiaohe.sakamichi.xinzhiying.utils.RequestUtils;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn_login;
    private Button mBtn_reg;
    private EditText mEt_id;
    private EditText mEt_pw;
    private TextView mTv_forget;
    private String mPhoneNum;
    private String mPassword;

    private String tag = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
    }

    private void initData() {
        mBtn_login.setOnClickListener(this);
        mBtn_reg.setOnClickListener(this);
        mTv_forget.setOnClickListener(this);
    }

    private void initView() {
        mBtn_login = (Button) findViewById(R.id.btn_login);
        mBtn_reg = (Button) findViewById(R.id.btn_reg);
        mEt_id = (EditText) findViewById(R.id.et_id);
        mEt_pw = (EditText) findViewById(R.id.et_pw);
        mTv_forget = (TextView) findViewById(R.id.tv_forget);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                LoginServer();
                break;
            case R.id.btn_reg:
                //跳转注册界面
                Intent intent_reg = new Intent(this, RegActivity.class);
                startActivity(intent_reg);
                break;
            case R.id.tv_forget:
                //跳转找回密码
                Intent intent = new Intent(this, ForgetActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void LoginServer() {
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
        RequestQueue requestQueue = RequestUtils.getInstance(this);
        //post请求时getParams无效 需通过String直接传参
        String body = "phone=" + mPhoneNum + "&pass=" + Md5Utils.encode(mPassword);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ConstantValues.LOGIN_URL, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.d(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(tag, error.getMessage());
            }
        }) {
            @Override
            public String getBodyContentType() {
                if (getMethod() == Method.POST) {
                    return "application/x-www-form-urlencoded";
                }
                return super.getBodyContentType();
            }
        };
        requestQueue.add(request);
    }
}
