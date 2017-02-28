package com.jiaohe.sakamichi.xinzhiying.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;


/**
 * Created by sakamichi on 16/9/14.
 */
public class SPUtils {

    public static void putBoolean(Context ctx, String key, Boolean value) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ConstantValues.CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value).apply();
    }

    public static Boolean getBoolean(Context ctx, String key, Boolean value) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ConstantValues.CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, value);
    }

    public static void putString(Context ctx, String key, String value) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ConstantValues.CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value).apply();
    }

    public static String getString(Context ctx, String key, String value) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(ConstantValues.CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, value);
    }

}
