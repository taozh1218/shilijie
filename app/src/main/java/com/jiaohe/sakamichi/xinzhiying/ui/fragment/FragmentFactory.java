package com.jiaohe.sakamichi.xinzhiying.ui.fragment;

import android.util.SparseArray;

/**
 * Created by sakamichi on 16/11/2.
 */

public class FragmentFactory {

    private static SparseArray<BaseFragment> mFragmentMap = new SparseArray<>();

    public static BaseFragment create(int pos) {
        BaseFragment mFragment = mFragmentMap.get(pos);
        if (mFragment == null) {
            switch (pos) {
                case 0:
                    mFragment = new FameFragment();
                    break;
               
            }
            mFragmentMap.put(pos, mFragment);
        }
        return mFragment;
    }

}
