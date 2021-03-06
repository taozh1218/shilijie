package com.jiaohe.sakamichi.xinzhiying.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.global.MyApplication;
import com.jiaohe.sakamichi.xinzhiying.ui.acitivity.CircleBackgroundImageActivity;
import com.jiaohe.sakamichi.xinzhiying.ui.view.AvatarImageView;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UriUtils;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String path = Environment.getExternalStorageDirectory() + "/crop_icon.jpg";
    private Context context;
    private ArrayList<String> list = new ArrayList<>();
    private final int TYPE_HEAD =0,TYPE_ITEM=1;
    //是否已经评论
    private boolean isComment=false;
    //是否已经点赞
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
                Uri uriFromFilePath = UriUtils.getUriFromFilePath(path);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriFromFilePath);
                    ((HeadViewHolder) holder).aiv_userIcon.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ((HeadViewHolder) holder).iv_circleBg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CircleBackgroundImageActivity.class);
                        context.startActivity(intent);
                    }
                });
                String imageId = SPUtils.getString(context, "imageId", "");


                if (!TextUtils.isEmpty(imageId)){
                    int id=Integer.parseInt(imageId);
                    switch (id){
                        case 1:
                            ((HeadViewHolder) holder).iv_circleBg.setImageResource(R.drawable.default_image1);
                            break;
                        case 2:
                            ((HeadViewHolder) holder).iv_circleBg.setImageResource(R.drawable.default_image2);
                            break;
                        case 3:
                            ((HeadViewHolder) holder).iv_circleBg.setImageResource(R.drawable.default_image3);
                            break;
                        case 4:
                            String path = MyApplication.getContext().getExternalCacheDir() + "/circle_bg.jpg";
                            Uri uriPath = UriUtils.getUriFromFilePath(path);
                            System.out.println(uriPath);
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriPath);
                                ((HeadViewHolder) holder).iv_circleBg.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }

            }else {
                ((OneViewHolder) holder).tv_userName.setText(list.get(position-1));
                ((OneViewHolder) holder).iv_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String trim = ((OneViewHolder) holder).tv_comment.getText().toString().trim();
                        int i = parseInt(trim);
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
        private ImageView iv_circleBg;
        private AvatarImageView aiv_userIcon;

        public HeadViewHolder(View itemView) {
            super(itemView);
            iv_circleBg= (ImageView) itemView.findViewById(R.id.iv_bgImage);
            aiv_userIcon= (AvatarImageView) itemView.findViewById(R.id.aiv_circleIcon);

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
