package com.jiaohe.sakamichi.xinzhiying.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;


/**
 * Created by sakamichi on 16/11/2.
 */

public abstract class LoadingLayout extends FrameLayout {
    //定义加载状态
    private static final int STATE_IDLE = 0;
    private static final int STATE_LOADING = 1;
    private static final int STATE_EMPTY = 2;
    private static final int STATE_FAIL = 3;
    private static final int STATE_SUCCESS = 4;

    private int mCurrStat = STATE_IDLE;

    //按当前状态填充给布局的view
    private View mLoadingView;
    private View mFailView;
    private View mEmptyView;
    private View mSuccessView;

    public LoadingLayout(Context context) {
        super(context);
        initView();
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        if (mLoadingView == null) {
            mLoadingView = UIUtils.inflate(R.layout.loading_layout);
            addView(mLoadingView);
        }

        if (mFailView == null) {
            mFailView = UIUtils.inflate(R.layout.fail_layout);
            //设置重新加载btn的点击事件
            Button btn_reload = (Button) mFailView.findViewById(R.id.btn_reload);
            btn_reload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData();
                }
            });
            addView(mFailView);
        }

        if (mEmptyView == null) {
            mEmptyView = UIUtils.inflate(R.layout.empty_layout);
            addView(mEmptyView);
        }

        showRightLayout(); //根据当前状态显示，隐藏特定布局
    }

    private void showRightLayout() {
        //使用三元表达式 设置可见性
        //加载中界面
        mLoadingView.setVisibility((mCurrStat == STATE_IDLE || mCurrStat == STATE_LOADING) ? VISIBLE : GONE);
        //加载失败界面
        mFailView.setVisibility(mCurrStat == STATE_FAIL ? VISIBLE : GONE);
        //加载返回空值界面
        mEmptyView.setVisibility(mCurrStat == STATE_EMPTY ? VISIBLE : GONE);
        //当加载成功布局为空且当前为加载成功状态时 初始化该布局
        if (mCurrStat == STATE_SUCCESS && mSuccessView == null) {
            mSuccessView = createSuccessLayout();
            if (mSuccessView != null) { //如果子类空实现 则返回null 故需非空判断
                addView(mSuccessView);
            }
        }

        //根据当前状态设置mSuccessView可见性
        if (mSuccessView != null) {
            mSuccessView.setVisibility(mCurrStat == STATE_SUCCESS ? VISIBLE : GONE);
        }
    }

    public void loadData() {
        if (mCurrStat != STATE_LOADING) { //如果当前未在加载则开始加载
            mCurrStat = STATE_LOADING;
            //请求网络 放在子线程中执行
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ResultStat resultStat = reqData();
                    //涉及更改ui运行到主线程
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultStat != null) {
                                int state = resultStat.getState();
                                //拿到网络请求返回结果并更新给当前状态
                                mCurrStat = state;
                                //更新状态后刷新页面以配合当前状态
                                showRightLayout();
                            }
                        }
                    });
                }
            }).start();
        }
    }

    //使用一个枚举管理网络请求的完成状态（枚举太耗内存一般不推荐在android中使用
    public enum ResultStat {
        STAT_SUCCESS(STATE_SUCCESS), STAT_FAIL(STATE_FAIL), STAT_EMPTY(STATE_EMPTY);

        private int state;

        ResultStat(int state) { //枚举中构造器必须私有
            //接收对象中的参数
            this.state = state;
        }

        public int getState() { //获取返回状态的窗口方法
            return state;
        }
    }

    //交由调用类具体实现成功布局 本类只负责加载
    public abstract View createSuccessLayout();

    //请求网络具体实现交也由调用者的子类完成
    protected abstract ResultStat reqData();
}
