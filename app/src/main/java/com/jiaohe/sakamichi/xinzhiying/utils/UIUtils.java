package com.jiaohe.sakamichi.xinzhiying.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Process;
import android.view.View;

import com.jiaohe.sakamichi.xinzhiying.global.MyApplication;


/**
 * Created by sakamichi on 16/10/31.
 */

public class UIUtils {
    //1.获取Application类中定义的字段（封装2层
    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static Handler getHandler() {
        return MyApplication.getHandler();
    }

    public static int getTid() {
        return MyApplication.getTid();
    }

    //2.获取资源文件对象（字符串/数组，图片，颜色，尺寸
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);
    }

    //3.dp/px互转方法
    public static int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static float px2dp(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    //4.加载布局文件封装
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    //5.判断是否运行在主线程
    public static boolean isUIThread() {
        //获取当前线程id
        int tid = Process.myTid();
        if (tid == getTid()) {
            return true;
        }

        return false;
    }

    //6.在主线程执行
    public static void runOnUIThread(Runnable runnable) {
        if (isUIThread()) {
            runnable.run(); //已经在主线程 直接运行
        } else {
            getHandler().post(runnable); //post到主线程运行
        }
    }

    //7.根据id获取颜色状态选择器（PagerTab要求
    public static ColorStateList getColorStateList(int tabTextColorResId) {
        return getContext().getResources().getColorStateList(tabTextColorResId);
    }
}
