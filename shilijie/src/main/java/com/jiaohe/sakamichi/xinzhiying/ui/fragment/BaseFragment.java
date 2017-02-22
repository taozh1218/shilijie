package com.jiaohe.sakamichi.xinzhiying.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiaohe.sakamichi.xinzhiying.ui.view.LoadingLayout;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;

import java.util.ArrayList;

/**
 * Created by sakamichi on 16/11/2.
 */

public abstract class BaseFragment extends Fragment {

    private LoadingLayout mLoadingLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //VP中所有fragment的父类 用一个FrameLayout填充以实现各种状态下的布局
        //调用者继续交由子类实现
        mLoadingLayout = new LoadingLayout(UIUtils.getContext()) {
            @Override
            protected ResultStat reqData() {
                return BaseFragment.this.reqData();
            }

            @Override
            public View createSuccessLayout() {
                //调用者继续交由子类实现
                return BaseFragment.this.createSuccessLayout();

            }
        };

        return mLoadingLayout;
    }

    protected abstract LoadingLayout.ResultStat reqData();

    public abstract View createSuccessLayout();

    public void loadData() {
        if (mLoadingLayout != null) {
            mLoadingLayout.loadData();
        }
    }

    //对网络返回数据合法性进行校验
    public LoadingLayout.ResultStat check(Object obj) {
        if (obj != null) {
            if (obj instanceof ArrayList) {//是否为集合
                ArrayList list = (ArrayList) obj;
                if (list.isEmpty()) {
                    return LoadingLayout.ResultStat.STAT_EMPTY;
                } else {
                    return LoadingLayout.ResultStat.STAT_SUCCESS;
                }
            }
        }
        return LoadingLayout.ResultStat.STAT_FAIL;
    }
}
