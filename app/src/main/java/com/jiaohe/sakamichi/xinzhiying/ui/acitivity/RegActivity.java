package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.jiaohe.sakamichi.xinzhiying.utils.RequestUtils;

import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * 
 */
public class RegActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CERT_CODE = "102";
    private String tag = "LoginActivity";

    private Button mBtn_cert;
    private Button mBtn_reg;
    private EditText mEt_id;
    private EditText mEt_cert;
    private EditText mEt_pw;
    private String mPhoneNum;
    private String mPassword;
    private String mCert;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        initView();
        initData();
    }

    private void initData() {
        mBtn_cert.setOnClickListener(this);
        mBtn_reg.setOnClickListener(this);
    }

    private void initView() {
        mBtn_cert = (Button) findViewById(R.id.btn_cert);
        mBtn_reg = (Button) findViewById(R.id.btn_reg);
        mEt_id = (EditText) findViewById(R.id.et_id);
        mEt_cert = (EditText) findViewById(R.id.et_cert);
        mEt_pw = (EditText) findViewById(R.id.et_pw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cert:
                mPhoneNum = mEt_id.getText().toString().trim();
                if (TextUtils.isEmpty(mPhoneNum)) { //手机号为空
                    Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    getCertificate();
                }
                
                break;
            case R.id.btn_reg:
                mPassword = mEt_pw.getText().toString().trim();
                mCert = mEt_cert.getText().toString().trim();
                if(TextUtils.isEmpty(mPhoneNum)||TextUtils.isEmpty(mCert)||TextUtils.isEmpty(mPassword)){
                    Toast.makeText(this, "用户名，密码，验证码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 注册用户
     */
    private void RegUser() {
        
    }

    private void getCertificate() {
        if (!checkNum(mPhoneNum)) {//手机号格式不合法
            Toast.makeText(this, "请正确填写手机号码", Toast.LENGTH_SHORT).show();
        } else {
            requestCert();
        }
    }


    /**
     * @param phoneNum 用户输入的手机号码
     * @return 格式正确返回true
     * 
     * 校验用户输入的手机号码格式是否合法
     */
    private boolean checkNum(String phoneNum) {
        String regex = "^[1][3,4,5,7,8][0-9]{9}$";
        return Pattern.matches(regex, phoneNum);
    }

    /**
     * 请求服务器发送用户注册验证码
     */
    private void requestCert() {
        mRequestQueue = RequestUtils.getInstance(this);
        String body = "phone=" + mPhoneNum + "&type=" + CERT_CODE;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ConstantValues.CERT_URL, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.d(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(tag,"获取失败 请重新获取验证码");
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
