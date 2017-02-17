package com.jiaohe.sakamichi.xinzhiying.util;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jiaohe.sakamichi.xinzhiying.global.MyApplication;

/**
 * Created by sakamichi on 16/9/21.
 */

public class RequestUtils {

    private RequestUtils() {
        super();
    }


    public static void postJsonRequest(String url, String body, Context ctx, VolleyInterface vi) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, vi.loadingListener(), vi.errorListener()) {
            @Override
            public String getBodyContentType() {
                if (getMethod() == Method.POST) {
                    return "application/x-www-form-urlencoded";
                }
                return super.getBodyContentType();
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance(ctx).add(request);
    }
}
