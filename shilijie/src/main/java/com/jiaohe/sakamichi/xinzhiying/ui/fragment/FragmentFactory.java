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
                case 1:
                    mFragment = new MapFragment();
                    break;
                /*case 2:
                    mFragment = new RelationFragment();
                    break;*/
                case 3:
                    mFragment = new CircleFragment();
                    break;
            }
            mFragmentMap.put(pos, mFragment);
        }
        return mFragment;
    }

}
