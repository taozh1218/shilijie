package com.jiaohe.sakamichi.xinzhiying.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.json.JSONObject;


/**
 * Created by sakamichi on 2017/2/16.
 */

public abstract class VolleyInterface {
    /**
     * 上下文
     */
    public Context mContext;
    /**
     * 请求成功监听
     */
    public static Listener<JSONObject> mResponseListener;
    /**
     * 请求失败监听
     */
    public static ErrorListener mErrorListener;

    public VolleyInterface(Context context, Listener<JSONObject> listener, ErrorListener errorListener) {
        this.mContext = context;
        this.mResponseListener = listener;
        this.mErrorListener = errorListener;
    }

    /**
     * 请求成功的抽象类
     *
     * @param response
     */
    public abstract void onSuccess(JSONObject response);

    /**
     * 请求失败的抽象类
     *
     * @param error
     */
    public abstract void onError(VolleyError error);

    /**
     * 请求成功监听
     *
     * @return
     */
    public Listener<JSONObject> loadingListener() {
        mResponseListener = new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject result) {
                Log.e("请求成功返回的数据：", result.toString());
                onSuccess(result);
            }
        };
        return mResponseListener;
    }

    /**
     * 请求失败监听
     *
     * @return
     */
    public ErrorListener errorListener() {
        mErrorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("请求失败返回的数据：", error.toString());
                onError(error);
            }
        };
        return mErrorListener;
    }
}
