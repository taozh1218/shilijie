package com.jiaohe.sakamichi.xinzhiying.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiaohe.sakamichi.xinzhiying.ui.view.LoadingLayout;

public class MapFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected LoadingLayout.ResultStat reqData() {
        return null;
    }

    @Override
    public View createSuccessLayout() {
        return null;
    }

    @Override
    public void loadData() {
        super.loadData();
    }

    @Override
    public LoadingLayout.ResultStat check(Object obj) {
        return super.check(obj);
    }
}
