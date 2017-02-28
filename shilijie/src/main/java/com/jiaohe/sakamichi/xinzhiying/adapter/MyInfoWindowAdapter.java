package com.jiaohe.sakamichi.xinzhiying.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;

/**
 * Created by DIY on 2017/2/25.
 */

public class MyInfoWindowAdapter implements AMap.InfoWindowAdapter, View.OnClickListener {

    private Context mContext;
    /**
     * 根布局，非布局文件指代的view
     */
    private LinearLayout mRoot_view;
    private ImageView img_avatar;
    private TextView tv_name;
    private TextView tv_phoneNo;
    private ImageView img_verification;
    /**
     * 签名，暂定是EditText
     */
    private EditText mEdt;
    /**
     * 存放的是经纬度信息
     */
    private TextView tv_address;
    private String snippet;

    private LatLng latLng;
    private String name;
    private String phone;
    /**
     * 暂存经纬度 lat+lng
     */
    private String address;
    private Marker mMarker;

    public MyInfoWindowAdapter(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
//            case R.id.navigation_LL:  //点击导航
//                NavigationUtils.Navigation(latLng);
//                break;
//
//            case R.id.call_LL:  //点击打电话
//                PhoneCallUtils.call(
//                break;

//            case R.id.tv_go_infoWindow:
////                getDrivingRoute(new LatLonPoint(mMarker.getPosition().latitude,mMarker.getPosition().longitude));
//                break;
//            case R.id.img_go_infoWindow:
//                break;
        }
    }



    /**
     *
     * @param marker
     * @return
     */
    @Override
    public View getInfoWindow(Marker marker) {

        View view = initView(marker);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = initView(marker);
        return view;
    }

    private void initData(Marker marker) {
        name = marker.getTitle();
//        address = marker.getSnippet();
        address = marker.getPosition().latitude + "+" + marker.getPosition().longitude;

    }


    @NonNull
    private View initView(Marker marker) {
        mMarker = marker;

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_infowindow, null);
        mRoot_view = (LinearLayout) view.findViewById(R.id.ll_infoWindow);
        img_avatar = (ImageView) view.findViewById(R.id.img_avatar_infoWindow);
        tv_name = (TextView) view.findViewById(R.id.tv_name_infoWindow);
        tv_phoneNo = (TextView) view.findViewById(R.id.tv_phoneNo_infoWindow);
        TextView tv_go = (TextView) view.findViewById(R.id.tv_go_infoWindow);
        img_verification = (ImageView) view.findViewById(R.id.img_verification_infoWindow);
        ImageView img_go = (ImageView) view.findViewById(R.id.img_go_infoWindow);
        mEdt = (EditText) view.findViewById(R.id.tv_signature_infoWindow);
//        mEdt.setKeyListener(null);
        tv_address = (TextView) view.findViewById(R.id.tv_address_infoWindow);

        //avatar,name,sign,latlng,
//        tv_name.setText(String.format(mContext.getString(R.string.agent_addr), snippet));
        tv_name.setText(marker.getSnippet());
        tv_phoneNo.setText(SPUtils.getString(mContext,"phone",""));
        tv_address.setText(marker.getPosition().latitude + "+" + marker.getPosition().longitude);

        tv_go.setOnClickListener(this);
        img_go.setOnClickListener(this);
        img_avatar.setOnClickListener(this);
        tv_name.setOnClickListener(this);
        return view;
    }



}
