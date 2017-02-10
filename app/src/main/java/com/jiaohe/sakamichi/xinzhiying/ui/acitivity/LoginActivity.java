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
import com.jiaohe.sakamichi.xinzhiying.utils.SPUtils;

import org.json.JSONException;
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
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        mRequestQueue = RequestUtils.getInstance(this);
        //post请求时getParams无需通过String直接传参
        String body = "phone=" + SPUtils.getString(this, "phone", "");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ConstantValues.CHECK_TOKEN_URL, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.d(response.toString());
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
        mRequestQueue.add(request);
        return isValid[0];
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
        mRequestQueue = RequestUtils.getInstance(this);
        //post请求时getParams无效 需通过String直接传参
        String body = "phone=" + mPhoneNum + "&pass=" + Md5Utils.encode(mPassword);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ConstantValues.LOGIN_URL, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.d(response.toString());
                try {
                    String result = response.getString("result");
                    if (result.equals("RC100")) {
                        LogUtils.d("登录成功");
                        //如果本地无缓存，则缓存返回的token和手机号码
                        SPUtils.putString(LoginActivity.this, "phone", mPhoneNum);
                        SPUtils.putString(LoginActivity.this, "token", response.getString("token"));
                        //登录成功跳转到主界面
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        LogUtils.d("登录失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
        mRequestQueue.add(request);
    }
}
