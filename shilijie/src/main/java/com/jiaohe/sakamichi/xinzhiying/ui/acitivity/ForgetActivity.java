package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import com.jiaohe.sakamichi.xinzhiying.util.Md5Utils;
import com.jiaohe.sakamichi.xinzhiying.util.RegexUtils;
import com.jiaohe.sakamichi.xinzhiying.util.RequestUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;
import com.jiaohe.sakamichi.xinzhiying.util.VolleyInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TYPE_CHANGE_PW = "101";
    private String tag = "ForgetActivity";


    private ImageButton mIb_back;
    private CountButton mBtn_cert;
    private Button mBtn_confirm;
    private EditText mEt_new_pw;
    //private EditText mEt_confirm_pw;
    private EditText mEt_phone;
    private EditText mEt_cert;
    private String mPhoneNum;
    private String mNewPW;
    //private String mConfirmPW;
    private String mCert;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        UIUtils.initStateBar(ForgetActivity.this);
        initView();
        initData();
    }

    private void initData() {
        mBtn_cert.setOnClickListener(this);
        mIb_back.setOnClickListener(this);
        mBtn_confirm.setOnClickListener(this);
    }

    private void initView() {
        mIb_back = (ImageButton) findViewById(R.id.ib_back);
        mBtn_cert = (CountButton) findViewById(R.id.btn_cert);
        mBtn_confirm = (Button) findViewById(R.id.btn_confirm);
        mEt_phone = (EditText) findViewById(R.id.et_phone);
        mEt_cert = (EditText) findViewById(R.id.et_cert);
        mEt_new_pw = (EditText) findViewById(R.id.et_new_pw);
        //mEt_confirm_pw = (EditText) findViewById(R.id.et_confirm_pw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_cert:
                mPhoneNum = mEt_phone.getText().toString().trim();
                if (TextUtils.isEmpty(mPhoneNum)) { //手机号为空
                    Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    getCertificate();
                }
                break;
            case R.id.btn_confirm:
                mNewPW = mEt_new_pw.getText().toString().trim();
                //mConfirmPW = mEt_confirm_pw.getText().toString().trim();
                mCert = mEt_cert.getText().toString().trim();

                if (TextUtils.isEmpty(mNewPW) /*|| TextUtils.isEmpty(mConfirmPW)*/) { //2次密码至少有一个为空
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mCert)) {
                    Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    //if (mNewPW.equals(mConfirmPW)) {
                    if (RegexUtils.checkPW(mNewPW)) {
                        mDialog = ProgressDialog.show(this, "", "正在提交...", true, true);
                        commitNewPW();
                    } else {
                        Toast.makeText(this, "密码必须为6-18位数字和字母组合", Toast.LENGTH_SHORT).show();
                    }
                    /*} else {
                        Toast.makeText(this, "2次密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
                    }*/
                }
                break;
        }
    }

    private void commitNewPW() {
        String body = "phone=" + mPhoneNum + "&pass=" + Md5Utils.encode(mNewPW) + "&code=" + mCert;
        RequestUtils.postJsonRequest(ConstantValues.FIND_PW_URL, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                String result = null;
                try {
                    result = response.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (result) {
                    case "RC100":
                        mDialog.dismiss();
                        Toast.makeText(ForgetActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case "RC200":
                        mDialog.dismiss();
                        Toast.makeText(ForgetActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                        break;
                    case "RC201":
                        mDialog.dismiss();
                        Toast.makeText(ForgetActivity.this, "参数不完整！", Toast.LENGTH_SHORT).show();
                        break;
                    case "RC300":
                        mDialog.dismiss();
                        Toast.makeText(ForgetActivity.this, "操作失败！", Toast.LENGTH_SHORT).show();
                        break;
                    case "RC403":
                        mDialog.dismiss();
                        Toast.makeText(ForgetActivity.this, "服务不可用！", Toast.LENGTH_SHORT).show();
                        break;
                    case "RC404":
                        mDialog.dismiss();
                        Toast.makeText(ForgetActivity.this, "验证码失效！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onError(VolleyError error) {
                VolleyLog.d(tag, "提交新密码失败");
            }
        });
    }

    private void getCertificate() {
        if (!RegexUtils.checkNum(mPhoneNum)) {//手机号格式不合法
            Toast.makeText(this, "请正确填写手机号码", Toast.LENGTH_SHORT).show();
        } else {
            requestCert();
            //禁用手机软键盘输入 以防修改手机号码
            mEt_phone.setInputType(InputType.TYPE_NULL);
        }
    }

    /**
     * 请求服务器发送用户注册验证码
     */
    private void requestCert() {
        String body = "phone=" + mPhoneNum + "&type=" + TYPE_CHANGE_PW;
        RequestUtils.postJsonRequest(ConstantValues.CERT_URL, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                //拿到短信后启用手机号码输入
                mEt_phone.setInputType(InputType.TYPE_CLASS_PHONE);
            }

            @Override
            public void onError(VolleyError error) {
                VolleyLog.d(tag, "获取失败 请重新获取验证码");
            }
        });
    }
}
