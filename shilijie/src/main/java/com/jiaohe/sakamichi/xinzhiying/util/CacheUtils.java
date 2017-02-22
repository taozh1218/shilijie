package com.jiaohe.sakamichi.xinzhiying.util;

import android.content.Context;

/**
 * Created by sakamichi on 16/9/24.
 */

public class CacheUtils {
    public static void setCache(String url, String json, Context ctx) {
        SPUtils.putString(ctx, url, json);
    }

    public static String getCache(String url, Context ctx) {
        return SPUtils.getString(ctx, url, null);
    }
}
