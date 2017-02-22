package com.jiaohe.sakamichi.xinzhiying.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.jiaohe.sakamichi.xinzhiying.R;


/**
 * Created by sakamichi on 2016/11/23.
 */

public class RatioLayout extends FrameLayout {

    private float mRatio;

    public RatioLayout(Context context) {
        super(context);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //1.第一种自定义参数获取方法
        //float ratio = attrs.getAttributeFloatValue("sakamichi", "ratio", -1); 
        //2.第二种自定义参数获取方法
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
        mRatio = typedArray.getFloat(R.styleable.RatioLayout_ratio, -1);
        typedArray.recycle(); //typedArray使用后必须关闭
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//获取子控件宽度模式
        int width = MeasureSpec.getSize(widthMeasureSpec);//获取子控件宽度尺寸
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);//获取子控件高度模式
        int height = MeasureSpec.getSize(heightMeasureSpec);//获取子控件宽度尺寸
        //宽确定 高不确定 ratio有值 才计算高度
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY && mRatio > 0) {
            //图片宽=控件宽-内边距
            int imageWidth = width - getPaddingLeft() - getPaddingRight();
            //图片高=宽/比例
            int imageHeight = (int) (imageWidth / mRatio + 0.5f);
            //计算控件高=图高+上下边距
            height = imageHeight + getPaddingTop() + getPaddingBottom();
            //根据计算出的高度重新生成heightMeasureSpec
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}


