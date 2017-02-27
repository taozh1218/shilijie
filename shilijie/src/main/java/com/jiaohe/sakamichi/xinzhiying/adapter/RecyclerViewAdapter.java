package com.jiaohe.sakamichi.xinzhiying.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiaohe.sakamichi.xinzhiying.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> list = new ArrayList<>();
    private final int TYPE_HEAD =0,TYPE_ITEM=1;
    //是否已经评论
    private boolean isComment=false;
    //shif已经点赞
    private boolean isGood=false;

    public RecyclerViewAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (getItemViewType(viewType)==TYPE_HEAD){
            View view = LayoutInflater.from(context).inflate(R.layout.layout_circle_head,parent, false);
            return new HeadViewHolder(view);
        }else if (viewType==TYPE_ITEM){
            View view = LayoutInflater.from(context).inflate(R.layout.layout_circle_item_one, parent,false);

            return new OneViewHolder(view);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position)==TYPE_HEAD){




            }else {
                ((OneViewHolder) holder).tv_userName.setText(list.get(position-1));
                ((OneViewHolder) holder).iv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String trim = ((OneViewHolder) holder).tv_comment.getText().toString().trim();
                        int i = Integer.parseInt(trim);
                        i++;
                        ((OneViewHolder) holder).tv_comment.setText(i+"");

                    }
                });
            }
    }




    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return TYPE_HEAD;
        }else {
            return TYPE_ITEM;
        }
    }

    //头视图
    class HeadViewHolder extends RecyclerView.ViewHolder{




        public HeadViewHolder(View itemView) {
            super(itemView);
        }
    }


    class OneViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_userName,tv_comment;
        private ImageView iv_comment,iv_good;

        public OneViewHolder(View itemView) {
            super(itemView);
            tv_userName= (TextView) itemView.findViewById(R.id.tv_userName);
            iv_comment= (ImageView) itemView.findViewById(R.id.im_comment);
            tv_comment= (TextView) itemView.findViewById(R.id.tv_commentNum);
            iv_good= (ImageView) itemView.findViewById(R.id.iv_good);

        }
    }
}
