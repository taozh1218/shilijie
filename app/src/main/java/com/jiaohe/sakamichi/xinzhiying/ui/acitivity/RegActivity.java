package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.global.MyApplication;
import com.jiaohe.sakamichi.xinzhiying.ui.view.CountButton;
import com.jiaohe.sakamichi.xinzhiying.util.Md5Utils;
import com.jiaohe.sakamichi.xinzhiying.util.RegexUtils;
import com.jiaohe.sakamichi.xinzhiying.util.RequestUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;
import com.jiaohe.sakamichi.xinzhiying.util.VolleyInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class RegActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TYPE_REG = "102";
    private String tag = "LoginActivity";

    private CountButton mBtn_cert;
    private Button mBtn_reg;
    private EditText mEt_id;
    private EditText mEt_cert;
    private EditText mEt_pw;
    private TextView mTv_hasAccount;

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
        mTv_hasAccount.setOnClickListener(this);
    }

    private void initView() {
        mBtn_cert = (CountButton) findViewById(R.id.btn_cert);
        mBtn_reg = (Button) findViewById(R.id.btn_reg);
        mEt_id = (EditText) findViewById(R.id.et_id);
        mEt_cert = (EditText) findViewById(R.id.et_cert);
        mEt_pw = (EditText) findViewById(R.id.et_pw);
        mTv_hasAccount = (TextView) findViewById(R.id.tv_hasAccount);
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
                mPhoneNum = mEt_id.getText().toString().trim();
                mPassword = mEt_pw.getText().toString().trim();
                mCert = mEt_cert.getText().toString().trim();
                //启用用户名et的手机软键盘
                mEt_id.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (TextUtils.isEmpty(mPhoneNum) || TextUtils.isEmpty(mCert) || TextUtils.isEmpty(mPassword)) {
                    Toast.makeText(this, "用户名，密码，验证码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (RegexUtils.checkPW(mPassword)) {
                        // checkCert(); //是否为有效验证码
                        regUser();
                    } else {
                        Toast.makeText(this, "密码必须为6-18位数字和字母组合", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv_hasAccount:
                finish(); //有密码则返回之前的登录页面
                break;
        }
    }

    /**
     * 注册用户
     */
    private void regUser() {
        String regBody = "phone=" + mPhoneNum + "&pass=" + Md5Utils.encode(mPassword) + "&code=" + mCert;
        RequestUtils.postJsonRequest(ConstantValues.REG_URL, regBody, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.getString("result");
                    switch (result) {
                        case "RC100":
                            Toast.makeText(MyApplication.getContext(), "恭喜您，注册成功！", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case "RC211":
                            Toast.makeText(MyApplication.getContext(), "该用户已存在！", Toast.LENGTH_SHORT).show();
                            break;
                        case "RC200":
                            Toast.makeText(MyApplication.getContext(), "注册失败！", Toast.LENGTH_SHORT).show();
                            break;
                        case "RC300":
                            Toast.makeText(MyApplication.getContext(), "操作异常！", Toast.LENGTH_SHORT).show();
                            break;
                        case "RC403":
                            Toast.makeText(MyApplication.getContext(), "服务不可用！", Toast.LENGTH_SHORT).show();
                            break;
                        case "RC404":
                            Toast.makeText(MyApplication.getContext(), "短信验证码失效！", Toast.LENGTH_SHORT).show();
                            break;
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

    private void getCertificate() {
        if (!RegexUtils.checkNum(mPhoneNum)) {//手机号格式不合法
            Toast.makeText(this, "请正确填写手机号码", Toast.LENGTH_SHORT).show();
        } else {
            requestCert();
            //禁用手机软键盘输入 以防修改手机号码
            mEt_id.setInputType(InputType.TYPE_NULL);
        }
    }

    /**
     * 请求服务器发送用户注册验证码
     */
    private void requestCert() {
        String body = "phone=" + mPhoneNum + "&type=" + TYPE_REG;
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
