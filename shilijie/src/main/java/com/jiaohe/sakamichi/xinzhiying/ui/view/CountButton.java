package com.jiaohe.sakamichi.xinzhiying.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sakamichi on 2017/2/9.
 */

public class CountButton extends Button implements View.OnClickListener {

    /*为了实现方便，我没有使用attrs声明相关的属性，源码不多，需要使用的小伙伴可以直接在源码中修改
        setDefaultTime(long defaultTime)方法设置倒计时总的计时时间，例如：sendTwo.setDefaultTime(60 * 1000);
        setDefaultText(String defaultText)方法设置点击按钮之前显示的文案，例如：sendTwo.setDefaultText("发送验证码");
        setFinishText(String finishText)方法设置倒计时完成之后显示的文案，例如：sendTwo.setFinishText("重新发送");
    */
    
    
    /*
     倒计时时长,默认计时时间
     */
    private long defaultTime = 60 * 1000;
    private long time = defaultTime;

    /*
    开始执行计时的类,可以每间隔一秒执行任务
     */
    private Timer timer;

    /*
    执行的任务
     */
    private TimerTask task;

    /*
    默认文案
     */
    private String defaultText = "获取验证码";
    /*
    计时完成之后显示的文案
     */
    private String finishText = "重新发送";


    /*
    点击事件
     */
    private OnClickListener onClickListener;

    public CountButton(Context context) {
        super(context);
        initView();
    }

    public CountButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CountButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        if (!TextUtils.isEmpty(getText())) {
            defaultText = getText().toString().trim();
        }
        this.setText(defaultText);
        setOnClickListener(this);
    }

    /*
    更新显示文案
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CountButton.this.setText(time / 1000 + "S后重新发送");
            time -= 1000;
            if (time < 0) {
                CountButton.this.setEnabled(true);
                CountButton.this.setText(finishText);
                clearTimer();
                time = defaultTime;
            }
        }
    };

    /*
    清除倒计时
     */
    private void clearTimer() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /*
    初始化时间
     */
    private void initTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
    }


/*
当activity或者fragment消亡的时候清除倒计时
 */

    @Override
    protected void onDetachedFromWindow() {
        clearTimer();
        super.onDetachedFromWindow();
    }


    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }


    public void setFinishText(String finishText) {
        this.finishText = finishText;
    }


    public void setDefaultTime(long defaultTime) {
        this.defaultTime = defaultTime;
    }

    @Override
    public void onClick(View view) {
        start();
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    public void start() {
        initTimer();
        this.setText(time / 1000 + " 秒");
        this.setEnabled(false);
        timer.schedule(task, 0, 1000);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l instanceof CountButton) {
            super.setOnClickListener(l);
        } else {
            this.onClickListener = l;
        }
    }
}
