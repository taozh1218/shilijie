package com.jiaohe.sakamichi.xinzhiying.util;

import android.util.Log;

/**
 * Created by DIY on 2017/2/25.
 */

public class MapUtil {
    public static final String KEY_PRIVATE = "90e4ea36e59a7bb0faa044a52c484efd";
    public static final String SIG_REQUEST_PARA = "sig";

    public static String getSign(String para){
        String sig = "MD5(para "+ KEY_PRIVATE+")";
        Log.d("MapUtil","sign:"+sig);
        return sig;
    }
}
