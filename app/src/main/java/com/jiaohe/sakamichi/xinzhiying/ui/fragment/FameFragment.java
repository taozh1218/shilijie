package com.jiaohe.sakamichi.xinzhiying.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.ui.view.LoadingLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class FameFragment extends BaseFragment {


    public FameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    protected LoadingLayout.ResultStat reqData() {
        return null;
    }

    @Override
    public View createSuccessLayout() {
        return null;
    }


}
