package com.jiaohe.sakamichi.xinzhiying.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by sakamichi on 16/9/21.
 */

public class RequestUtils {
    private static RequestQueue mRequestQueue;

    private RequestUtils() {
        super();
    }

    public static synchronized RequestQueue getInstance(Context ctx) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(ctx);
        }
        return mRequestQueue;
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
        getInstance(ctx).add(request);
    }
}
