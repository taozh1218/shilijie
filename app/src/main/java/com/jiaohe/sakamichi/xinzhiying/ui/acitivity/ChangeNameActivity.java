package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.bean.UserInfoBean;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.util.RequestUtils;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeNameActivity extends AppCompatActivity {

    private EditText mEdt_name;
    private String phone;
    private String TAG = "ChangeNameAct";
    private String token;
    private Gson mGson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);


        init();
    }


    private void init() {
        ImageView img_back = (ImageView) findViewById(R.id.img_back_changeNameAct);
        Button btn_save = (Button) findViewById(R.id.btn_save_changeNameAct);
        mEdt_name = (EditText) findViewById(R.id.edt_username_changeNameAct);

        img_back.setOnClickListener(mOnClickListener);
        btn_save.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.img_back_changeNameAct:
                    ChangeNameActivity.this.finish();
                    break;
                case R.id.btn_save_changeNameAct:
                    String name = mEdt_name.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getApplicationContext(), "不能为空！", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    UserInfoBean userInfoBean = new UserInfoBean();
                    userInfoBean.setUsername(name);
                    mGson = new Gson();
                    String json = mGson.toJson(userInfoBean);
                    requestServer(json);
                    break;
            }
        }
    };

    private void requestServer(String json) {
//        isValidated();

        phone = SPUtils.getString(this, "phone", "");
        token = SPUtils.getString(this, "token", "");
        RequestQueue requestQueue = RequestUtils.getInstance(this);
        //post请求时setParams无效 需通过String直接传参
        String body = "phone=" + phone + "&token=" + token + "&data=" + json;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ConstantValues.CHANGE_USER_INFO, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (result.equals("RC100")) {
                        SPUtils.putString(getApplicationContext(), "nickname", mEdt_name.getText().toString());
                        SPUtils.putBoolean(getApplicationContext(),"isResult",true);
                    }
                    Log.d(TAG, "onResponse(),result:" + result);
                    toast(result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String response = error.getMessage();
                Log.d(TAG, "onErrorResponse():" + response);
                toast(response);
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

    /**
     * Toast
     *
     * @param response 服务器返回的数据(String)
     */
    public void toast(String response) {
        //解析结果
        if (!TextUtils.isEmpty(response)) {
            //将返回码转换为具体信息
            final String toast = ToastUtil.getChangeUserInfoResult(response);
            Log.d(TAG, "toast(),toast:" + toast);
            Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
