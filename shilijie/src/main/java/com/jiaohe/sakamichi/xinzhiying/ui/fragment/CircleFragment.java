package com.jiaohe.sakamichi.xinzhiying.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.adapter.RecyclerViewAdapter;
import com.jiaohe.sakamichi.xinzhiying.ui.view.LoadingLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CircleFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<String> list;
    private boolean isSlidingToLast=false;
    String s;
    private RecyclerViewAdapter adapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };


    public CircleFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_circle, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();


    }

    private void initData() {
        list= new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            list.add("测试"+i);
        }
        System.out.println(list.size());

    }

    private void initView() {
        mSwipeRefreshLayout= (SwipeRefreshLayout) getView().findViewById(R.id.srl_SwipeRefreshLayout);
        mRecyclerView= (RecyclerView) getView().findViewById(R.id.rlv_recyclerView);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.circle_refresh_one,R.color.circle_refresh_two);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter=new RecyclerViewAdapter(getActivity(),list);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    int itemCount = layoutManager.getItemCount();
                    System.out.println("-------"+lastCompletelyVisibleItemPosition+"----"+itemCount);
                    if (lastCompletelyVisibleItemPosition==(itemCount-1)&&isSlidingToLast){
                        list.add("测试刷新");
                        adapter.notifyDataSetChanged();
                    }
                }


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                System.out.println("-------"+dx+"----"+dy);
                            if (dy>0){
                                isSlidingToLast=true;
                            }else {
                                isSlidingToLast=false;
                            }

            }
        });

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

    @Override
    public void onRefresh() {

    handler.sendEmptyMessageDelayed(0,4000);


    }
}
