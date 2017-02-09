package com.jiaohe.sakamichi.xinzhiying.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
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
}
