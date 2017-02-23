package com.jiaohe.sakamichi.xinzhiying.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.ui.view.LoadingLayout;

public class MapFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
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
