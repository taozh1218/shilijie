package com.jiaohe.sakamichi.xinzhiying.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.exceptions.HyphenateException;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.ui.view.NoScrollViewPager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RelationFragment extends Fragment {

    private NoScrollViewPager mVp_chat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_relation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        RadioGroup rg_selector = (RadioGroup) getView().findViewById(R.id.rg_selector);
        mVp_chat = (NoScrollViewPager) getView().findViewById(R.id.vp_chat);
        ChatFragmentAdapter adapter = new ChatFragmentAdapter(getFragmentManager());
        mVp_chat.setAdapter(adapter);
        mVp_chat.setPagingEnabled(false);
        //绑定切换器和轮播
        rg_selector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_chat_list:
                        mVp_chat.setCurrentItem(0, false);
                        break;
                    case R.id.rb_contact:
                        mVp_chat.setCurrentItem(1, false);
                        break;
                }
            }
        });
        //默认选中聊天界面rb
        rg_selector.check(R.id.rb_chat_list);
    }

    class ChatFragmentAdapter extends FragmentPagerAdapter {

        public ChatFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {//切换到会话列表碎片
                EaseConversationListFragment conversationListFragment = new EaseConversationListFragment();
                conversationListFragment.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

                    @Override
                    public void onListItemClicked(EMConversation conversation) {
                        //跳转到会话界面
                        //startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName()));
                    }
                });
                return conversationListFragment;
            } else {//切换到好友列表碎片
                EaseContactListFragment contactListFragment = new EaseContactListFragment();
                //需要设置联系人列表才能启动fragment
                contactListFragment.setContactsMap(getContacts());
                //设置item点击事件
                contactListFragment.setContactListItemClickListener(new EaseContactListFragment.EaseContactListItemClickListener() {
                    @Override
                    public void onListItemClicked(EaseUser user) {
                        // startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));
                    }
                });
                return contactListFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    //拉取联系人
    private Map<String, EaseUser> getContacts() {
        Map<String, EaseUser> map = new HashMap<>();
        try {
            List<String> userNames = EMClient.getInstance().contactManager().getAllContactsFromServer();
            for (String userId : userNames) {
                map.put(userId, new EaseUser(userId));
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return map;
    }

}
