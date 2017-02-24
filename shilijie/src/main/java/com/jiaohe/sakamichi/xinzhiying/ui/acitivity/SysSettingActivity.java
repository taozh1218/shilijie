package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.util.LogUtils;
import com.jiaohe.sakamichi.xinzhiying.util.Md5Utils;
import com.jiaohe.sakamichi.xinzhiying.util.RegexUtils;
import com.jiaohe.sakamichi.xinzhiying.util.RequestUtils;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;
import com.jiaohe.sakamichi.xinzhiying.util.VolleyInterface;

import org.json.JSONException;
import org.json.JSONObject;



public class SysSettingActivity extends AppCompatActivity implements View.OnClickListener{


    private static final String TYPE_REG = "101";
    private String tag = "SysSettingActivity";

    private String phone,token;
    private ImageButton mIb_back;
    private Button mBt_resetPhone,mBt_resetPwd;
    private PopupWindow pwdPopupWindow,phonePopupWindow;
    private View pwdPopup,phonePopup;

    //修改电话控件
    private TextView mTv_oldPhone;
    private EditText mEt_pwd,mEt_phoneCode;
    private Button mBt_phoneCode,mBt_rePhone;
    //修改密码控件
    private EditText mEt_pwdPhone,mEt_pwdCode,mEt_newPwd;
    private Button mBt_pwdCode,mBt_rePwd;

    private String pwdCode,newPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_setting);
        UIUtils.initStateBar(this);
        initData();
        initView();
    }
    private void initView() {
        mIb_back= (ImageButton) findViewById(R.id.ib_settingback);
        mIb_back.setOnClickListener(this);
        mBt_resetPhone= (Button) findViewById(R.id.button_resetPhone);
        mBt_resetPhone.setOnClickListener(this);
        mBt_resetPwd= (Button) findViewById(R.id.button_resetPwd);
        mBt_resetPwd.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_resetPhone:
                showResetPhone();
                break;
            case R.id.button_resetPwd:
                showResetPwd();
                break;
            case R.id.ib_settingback:
                finish();
                break;
            case R.id.button_phoneCert:
                //获取修改电话验证码
                getResetPhoneCode();
                break;
            case R.id.btn_resetPhone:
                resetPhone();
                break;
            case R.id.button_pwdCert:
                //获取修改密码验证码
                getResetPwdCode();
                break;
            case R.id.btn_resetPwd:
                pwdCode= mEt_pwdCode.getText().toString().trim();
                newPwd=mEt_newPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwdCode)||TextUtils.isEmpty(newPwd)){
                    Toast.makeText(this, "请将信息填写完整", Toast.LENGTH_SHORT).show();
                }else{

                    resetPwd();

                }

                break;
        }

    }
    private void resetPwd() {
        String body = "phone="+phone+"&pass="+Md5Utils.encode(newPwd)+ "&token="+token+"&code="+pwdCode;
        RequestUtils.postJsonRequest(ConstantValues.USER_RESET_PWD, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(),VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (result.equals("RC100")){
                        Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_LONG).show();
                        pwdPopupWindow.dismiss();

                    }else {
                        Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(VolleyError error) {
            }
        });



    }

    public void getResetPwdCode() {
        String body = "phone=" + phone + "&type=" + TYPE_REG;
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


    private void resetPhone() {



    }
    public void getResetPhoneCode() {




    }



    private void initData() {
        phone= SPUtils.getString(this,"phone","");
        token=SPUtils.getString(this,"token","");
    }

    //退出账号
    public void signOut(View view) {
        String body = "phone="+phone+"&token="+token;
        RequestUtils.postJsonRequest(ConstantValues.USER_LOGOUT_URL, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(),VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (result.equals("RC100")){
                        Toast.makeText(getApplicationContext(),"退出成功",Toast.LENGTH_LONG).show();
                        //退出成功跳转到登录页面并清除本地token缓存
                        Intent intent = new Intent(SysSettingActivity.this,LoginActivity.class);
                        SPUtils.putString(getApplicationContext(),"phone",null);
                        SPUtils.putString(getApplicationContext(),"token",null);
                        finish();
                        startActivity(intent);
                    }else {
                        Toast.makeText(getApplicationContext(), "退出失败，请重试", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(VolleyError error) {
            }
        });

    }


    private void showResetPwd() {
        pwdPopup = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_resetpwd, null);
        pwdPopupWindow = new PopupWindow(pwdPopup, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
        pwdPopupWindow.setFocusable(true);
        pwdPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        pwdPopupWindow.showAtLocation(mIb_back, Gravity.CENTER, 0, 0);
        //修改密码控件初始化
        mEt_pwdCode= (EditText) pwdPopup.findViewById(R.id.et_pwdCode);
        mEt_newPwd= (EditText) pwdPopup.findViewById(R.id.et_newPwd);
        mBt_pwdCode= (Button) pwdPopup.findViewById(R.id.button_pwdCert);
        mBt_pwdCode.setOnClickListener(this);
        mBt_rePwd= (Button) pwdPopup.findViewById(R.id.btn_resetPwd);
        mBt_rePwd.setOnClickListener(this);
        TextView tv_phone = (TextView) pwdPopup.findViewById(R.id.tv_phone);
        tv_phone.setText(phone);

    }

    private void showResetPhone() {
        phonePopup = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_resetphone, null);
        phonePopupWindow = new PopupWindow(phonePopup, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
        //修改电话号码 控件初始化
        mTv_oldPhone= (TextView) phonePopup.findViewById(R.id.tv_oldPhone);
        mEt_phoneCode= (EditText) phonePopup.findViewById(R.id.et_phonecode);
        mBt_phoneCode= (Button) phonePopup.findViewById(R.id.button_phoneCert);
        mBt_phoneCode.setOnClickListener(this);
        mBt_rePhone= (Button) phonePopup.findViewById(R.id.btn_resetPhone);
        mBt_rePhone.setOnClickListener(this);
        phonePopupWindow.setFocusable(true);
        phonePopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        phonePopupWindow.showAtLocation(mIb_back, Gravity.CENTER, 0, 0);
    }




}
