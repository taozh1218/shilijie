package com.jiaohe.sakamichi.xinzhiying.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

/**
 * Created by sakamichi on 16/10/2.
 */

public class MyApplication extends Application {
    private static Context mContext;
    private static Handler mHandler;
    private static int mTid;

    @Override
    public void onCreate() {
        //全局上下文
        mContext = getApplicationContext();
        //全局handler
        mHandler = new Handler();
        //全局主线程对象
        mTid = Process.myTid();
    }

    //为全局变量设置getter
    public static Handler getHandler() {
        return mHandler;
    }

    public static int getTid() {
        return mTid;
    }

    public static Context getContext() {
        return mContext;
    }
}
