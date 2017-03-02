package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.util.Timer;
import java.util.TimerTask;

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
    private boolean isExit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UIUtils.initStateBar(LoginActivity.this);
        //判断token是否存在
        isLogIn();

        initView();
        initKeyBord();
        initData();
    }

    private void initKeyBord() {
            //进入登录页面直接弹出软键盘
        mEt_id.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() { //弹出软键盘的代码
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEt_id, InputMethodManager.RESULT_SHOWN);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 0); //设置300毫秒的时长

    }

    private void isLogIn() {
        String token = SPUtils.getString(this, "token", null);
        isTokenValid(); //token是否有效
        if (token != null || isTokenValid()) {
            //登录成功跳转到主界面
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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
                InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEt_id.getWindowToken() , 0);
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(); // 调用双击退出函数
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {// isExit == false的简化版
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }
}
