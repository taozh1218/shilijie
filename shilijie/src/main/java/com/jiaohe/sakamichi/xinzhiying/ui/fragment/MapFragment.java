package com.jiaohe.sakamichi.xinzhiying.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.RouteSearch;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.adapter.MyInfoWindowAdapter;
import com.jiaohe.sakamichi.xinzhiying.bean.UserLocationBean;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.global.MyApplication;
import com.jiaohe.sakamichi.xinzhiying.ui.acitivity.DriveRouteActivity;
import com.jiaohe.sakamichi.xinzhiying.ui.overlay.MyPoiOverlay;
import com.jiaohe.sakamichi.xinzhiying.ui.view.LoadingLayout;
import com.jiaohe.sakamichi.xinzhiying.util.AMapUtil;
import com.jiaohe.sakamichi.xinzhiying.util.Constants;
import com.jiaohe.sakamichi.xinzhiying.util.LogUtils;
import com.jiaohe.sakamichi.xinzhiying.util.Md5Utils;
import com.jiaohe.sakamichi.xinzhiying.util.RequestUtils;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.ToastUtil;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UriUtils;
import com.jiaohe.sakamichi.xinzhiying.util.VolleyInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.jiaohe.sakamichi.xinzhiying.global.ConstantValues.GET_AROUND_SERVICE;
import static com.jiaohe.sakamichi.xinzhiying.util.BitmapUtil.toRoundBitmap;


public class MapFragment extends BaseFragment implements AMap.OnPOIClickListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener, LocationSource, AMap.OnInfoWindowClickListener, AMap.OnMapLongClickListener, AMap.OnMapLoadedListener, GeocodeSearch.OnGeocodeSearchListener, PoiSearch.OnPoiSearchListener, AMapLocationListener {


    private static final String TAG = "MapFragment";
    //
    public Context mContext;
    private static MapFragment fragment = null;
    private View mapLayout;

    private AMap mAMap;
    private UiSettings mUiSettings;
    //    @ViewInject(R.id.fragment_map)
    private MapView mMapView;
    private MyInfoWindowAdapter adapter;
    private Marker current_marker;//之前的marker
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation lastKnownLocation;
    private LatLonPoint lp;
    private double mLatitude_current;
    private double mLongitude_current;
    private GeocodeSearch geocoderSearch;

    private ProgressDialog progDialog;
    private EditText mEdt_search;
    //底部详情
    private RelativeLayout mPoiDetailView;
    private TextView mPoiName;
    private TextView mPoiAddress;
    private TextView mPoiInfo;
    private TextView mTv_latlng;
    //POI search poiSearchListener
    private String keyWord;
    private int currentPage;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    //result
    private PoiResult poiResult;
    private ArrayList<PoiItem> poiItems;

    private Marker geoMarker;
    private MyPoiOverlay poiOverlay;

    private UserLocationBean mUserLocationBean;
    private ArrayList<UserLocationBean> mUserLocationBeans;
    private final String path = Environment.getExternalStorageDirectory() + "/crop_icon.jpg";
    private RequestQueue mQueue;
    private LinearLayout mNavigationView;


    public static MapFragment newInstance() {
        if (fragment == null) {
            synchronized (MapFragment.class) {
                if (fragment == null) {
                    fragment = new MapFragment();
                }
            }
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        // Inflate the layout for this fragment
        if (mapLayout == null) {
//            mapLayout = inflater.inflate(R.layout.fragment_map, null);
//            ViewUtils.inject(this, mapLayout);
            mapLayout = inflater.inflate(R.layout.fragment_map, container, false);
            mMapView = (MapView) mapLayout.findViewById(R.id.map_poiClickAct);//findMap
            mMapView.onCreate(savedInstanceState);//创建地图，必须重写

            mQueue = Volley.newRequestQueue(mContext);

            init();
//            initView();
        }
        return mapLayout;
    }


    public void init() {
        initMap();
        //init View
        mEdt_search = (EditText) mapLayout.findViewById(R.id.edt_search_mapFrag);
        Button btn = (Button) mapLayout.findViewById(R.id.btn_search_mapFrag);

        mPoiDetailView = (RelativeLayout) mapLayout.findViewById(R.id.rl_detail_mapFrag);
        mPoiName = (TextView) mapLayout.findViewById(R.id.tv_detail_name_mapFrag);
        mPoiAddress = (TextView) mapLayout.findViewById(R.id.tv_detail_address_mapFrag);
        mPoiInfo = (TextView) mapLayout.findViewById(R.id.tv_detail_info_mapFrag);
        mTv_latlng = (TextView) mapLayout.findViewById(R.id.tv_detail_latlng_mapFrag);
        mNavigationView = (LinearLayout) mapLayout.findViewById(R.id.ll_navigation_mapFrag);

        btn.setOnClickListener(mOnClickListener);
//        mPoiDetailView.setOnClickListener(mOnClickListener);
        mEdt_search.setOnClickListener(mOnClickListener);
        mNavigationView.setOnClickListener(mOnClickListener);
    }


    private void initMap() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mUiSettings = mAMap.getUiSettings();
        }
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);

        mUiSettings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mUiSettings.setCompassEnabled(true);//指南针
        mUiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示

        mAMap.setOnMapClickListener(this);
        mAMap.setOnMapLongClickListener(this);
        mAMap.setOnMarkerClickListener(this);
        mAMap.setOnInfoWindowClickListener(this);
        mAMap.setOnPOIClickListener(this);
        mAMap.setOnMapLoadedListener(this);
        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);

        //自定义InfoWindow
        adapter = new MyInfoWindowAdapter(mContext);
        mAMap.setInfoWindowAdapter(adapter);
//        if (mLocationClient != null) {
//            addMarkerToMap(new LatLng(mLatitude_current, mLongitude_current), "上海", "中国上海市");
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume(); //管理地图的生命周期
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause(); //管理地图的生命周期
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy(); //管理地图的生命周期
        }
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    //地图的点击事件
    @Override
    public void onMapClick(LatLng latLng) {
        //点击地图上没marker 的地方，隐藏infoWindow
        if (current_marker != null) {
            current_marker.hideInfoWindow();
            whetherToShowDetailInfo(false);
//            current_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.default_icon));
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        clearAll();
        //通过经纬度获取位置信息
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latLng.latitude, latLng.longitude), 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        mTv_latlng.setText(latLng.latitude + "+" + latLng.longitude);
        geocoderSearch.getFromLocationAsyn(query);
//        showDialog();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        Log.i(TAG, "onMarkerClick()");
        current_marker = marker;
        jumpPoint(marker);

        if (!(marker.getObject() instanceof UserLocationBean)) {
//            Log.i(TAG, "onMarkerClick()," + marker.getTitle());

            mPoiName.setText(marker.getTitle());
            mPoiAddress.setText(marker.getSnippet());
            mTv_latlng.setText(marker.getPosition().latitude + "+" + marker.getPosition().longitude);

            whetherToShowDetailInfo(true);
            return true;
        }
        return false; //返回 “false”，除定义的操作之外，默认操作也将会被执行
    }

    private void insertUserInfo() {
        String body = "phone=" + SPUtils.getString(mContext, "phone", "") + "&token=" + SPUtils.getString(mContext, "token", "");
        RequestUtils.postJsonRequest(ConstantValues.GET_LOCATION_INFO, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.getString("result");
                    String info = response.getString("position");
                    if (result.equals("RC100")) {
                        if (!TextUtils.isEmpty(info)) {
                            Gson gson = new Gson();
                            mUserLocationBean = gson.fromJson(info, UserLocationBean.class);
                            Log.i(TAG, mUserLocationBean.toString());
                            mHandler.sendEmptyMessage(Constants.HANDLER_SHOW_DETAILVIEW);
                        }
                        //如果本地无缓存，则缓存返回的token和手机号码
                        //登录成功跳转到主界面
                    } else {
                        LogUtils.d("插入失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                VolleyLog.d(TAG, error.getMessage());
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case Constants.HANDLER_SHOW_DETAILVIEW:
                    //infoWindow
                    if (current_marker != null) {

                    }

                    break;
            }
        }
    };

    /**
     * 插入数据
     * <p>
     * TODO 签名
     *
     * @param lat 经度
     * @param lng 纬度
     */
    private void insertUserInfo(String lat, String lng) {
        String body = "phone=" + SPUtils.getString(mContext, "phone", "") + "&token=" + Md5Utils.encode(SPUtils.getString(mContext, "token", "")) + "&latitude=" + lat + "&longitude=" + lng;
        RequestUtils.postJsonRequest(ConstantValues.SUBMIT_LOCATION_INFO, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (result.equals("RC100")) {
                        //如果本地无缓存，则缓存返回的token和手机号码
                        //登录成功跳转到主界面
                        Log.i(TAG, "插入成功！");
                    } else {
                        Log.e(TAG, "insertUserInfo()失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                VolleyLog.d(TAG, error.getMessage());
            }
        });
    }

    /**
     * @param latLng
     * @param title
     * @param snippet
     */
    private void addMarkerToMap(LatLng latLng, String title, String snippet) {
        mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.default_icon))
        );
    }

    /**
     * 启动定位
     * <p>
     * 点击定位按钮，也会触发此回调
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(AMapUtil.ZoomLevel));
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(mContext);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式   AMapLocationClientOption.AMapLocationMode.Hight_Accuracy,AMapLocationMode.Battery_Saving,AMapLocationMode.Device_Sensors
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setNeedAddress(true);//返回地址信息
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();//启动定位
        }
    }


    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                Log.d(TAG, "onLocationChanged()&aMapLocation!=null");
                //重置当前坐标
                lp = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mLatitude_current = aMapLocation.getLatitude();
                mLongitude_current = aMapLocation.getLongitude();

//                mAMap.moveCamera(CameraUpdateFactory.zoomTo(AMapUtil.ZoomLevel));
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点

                //TODO 搜索周边的服务
                searchAroundService(3, mLatitude_current, mLongitude_current);
//                searchPoiNeighborhood();

//                addMarkerToMap(new LatLng(mLatitude_current, mLongitude_current), "上海", "中国上海市");
                Log.d(TAG, "onLocationChanged(),city:" + aMapLocation.getCity() +
                        "，province：" + aMapLocation.getProvince() +
                        ",country:" + aMapLocation.getCountry() +
                        ",getLocationDetail:" + aMapLocation.getLocationDetail()
                );
            }
        }
    }

    /**
     * 搜索周围的服务
     * <p>
     * 返回json数组
     *
     * @param meters 周围的距离
     * @param lat    经度
     * @param lng    纬度
     */
    private void searchAroundService(int meters, double lat, double lng) {
//        String body = "phone=" + "15680731371" + "&token=" + "239D0E35A9E14D0CA54542C07594E2BD" + "&latitude=" + lat + "&longitude=" + lng + "&length=" + meters;
        String body = "phone=" + SPUtils.getString(mContext, "phone", "") + "&token=" + SPUtils.getString(mContext, "token", "") + "&latitude=" + lat + "&longitude=" + lng + "&length=" + meters;
        Log.i(TAG, "searchAroundService(),body:" + body);
        RequestUtils.postJsonRequest(GET_AROUND_SERVICE, body, UIUtils.getContext(), new VolleyInterface(UIUtils.getContext(), VolleyInterface.mResponseListener, VolleyInterface.mErrorListener) {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (result.equals("RC100")) {
//                        Log.d(TAG,"response:"+response);
                        mUserLocationBeans = new ArrayList<UserLocationBean>();
                        JSONArray positionList = response.getJSONArray("positionList");
                        if (positionList.length() == 0) {
                            return;
                        }
                        Gson gson = new Gson();
                        for (int i = 0; i < positionList.length(); i++) {
                            JSONObject jsonObject = positionList.getJSONObject(i);
                            UserLocationBean userLocationBean = gson.fromJson(String.valueOf(jsonObject), UserLocationBean.class);
                            mUserLocationBeans.add(userLocationBean);
//                            Log.e(TAG, userLocationBean.toString());
                        }
                        setMarker();
                    } else {
                        Log.e(TAG, response.getString("result"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                VolleyLog.d(TAG, error.getMessage());
            }
        });
    }

    /**
     * 从服务器获取玩头像后，
     * 设置marker到map
     */
    private void setMarker() {
        //遍历list,下载图片
        for (UserLocationBean userLocationBean : mUserLocationBeans) {
            String headimgurl = userLocationBean.getUserinfo().getHeadimgurl();
            if (TextUtils.isEmpty(headimgurl)) {
                MarkerOptions markerOption = new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(userLocationBean.getLatitude()), Double.parseDouble(userLocationBean.getLongitude())))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.default_icon))
                        //TODO 暂定，将marker的title设置为用户名，snippet设置为phone，然后在点击infoWindow的时候，通过经纬度获取具体街道信息
                        .title(userLocationBean.getUserinfo().getUsername()).snippet(userLocationBean.getUserinfo().getPhone());
                //为marker设置自定义属性
                Marker marker = mAMap.addMarker(markerOption);
                userLocationBean.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_icon));
                marker.setObject(userLocationBean);

                Log.d(TAG, "添加到地图成功！");
            } else
                downloadIcon(userLocationBean);
        }
    }

    private void downloadIcon(final UserLocationBean userLocationBean) {
        final MarkerOptions markerOption = new MarkerOptions()//一定不能设置全局的，不然只会显示一个
                .position(new LatLng(Double.parseDouble(userLocationBean.getLatitude()), Double.parseDouble(userLocationBean.getLongitude())))
                //暂定，将marker的title设置为用户名，snippet设置为phone，然后在点击infoWindow的时候，通过经纬度获取具体街道信息
                .title(userLocationBean.getUserinfo().getUsername()).snippet(userLocationBean.getUserinfo().getPhone());
        ImageRequest imageRequest = new ImageRequest(
                userLocationBean.getUserinfo().getHeadimgurl(),//url
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Log.i(TAG, "-----downloadIcon():userLocationBean.getLatitude()," + userLocationBean.getLatitude() + ",userLocationBean.getlng" + userLocationBean.getLongitude() + ",url:" + userLocationBean.getUserinfo().getHeadimgurl());
                        //to round
//                        AvatarImageView avatarImageView = new AvatarImageView(mContext);
//                        avatarImageView.toRoundCorner(response,);

                        markerOption.icon(BitmapDescriptorFactory.fromBitmap(toRoundBitmap(response)));

                        Marker marker = mAMap.addMarker(markerOption);
                        userLocationBean.setBitmap(response);

                        marker.setObject(userLocationBean);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "downloadIcon():" + error.toString());
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.default_icon));
                Marker marker = mAMap.addMarker(markerOption);
                userLocationBean.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_icon));
                marker.setObject(userLocationBean);
            }
        });
        mQueue.add(imageRequest);
    }

    /**
     * 在地图上添加marker
     */
    private void downIcon(final String id, final String secret, final String securityToken) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String endpoint = "http://oss.xinzhiying.net";
                // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
                OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(id, secret, securityToken);
                OSS oss = new OSSClient(UIUtils.getContext(), endpoint, credentialProvider);
                GetObjectRequest get = new GetObjectRequest("jiaohe", "images/app/headimg/" + SPUtils.getString(UIUtils.getContext(), "phone", "") + "_icon");
                try {
                    GetObjectResult getResult = oss.getObject(get);
                    //
                    InputStream inputStream = getResult.getObjectContent();
                    OutputStream os = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "crop_icon.jpg"));
                    byte[] buffer = new byte[2048];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据，比如图片展示或者写入文件等
                        os.write(buffer, 0, len);
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Uri uriFromFilePath = UriUtils.getUriFromFilePath(path);
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uriFromFilePath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            mIv_icon.setImageBitmap(bitmap);
//                            mIv_slide_icon.setImageBitmap(bitmap);
                        }
                    });
                    SPUtils.putBoolean(MyApplication.getContext(), "isCache", true);
                    os.close();
                    inputStream.close();
                } catch (ClientException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 自定义marker添加到map
     *
     * @param latLng
     */
    private void addMarkersToMap(LatLng latLng) {
        // 文字显示标注，可以设置显示内容，位置，字体大小颜色，背景色旋转角度
        TextOptions textOptions = new TextOptions()
                .position(latLng)
                .text("Text")
                .fontColor(Color.BLACK)
                .backgroundColor(Color.BLUE)
                .fontSize(30)
                .rotate(20)
                .align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
                .zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
        mAMap.addText(textOptions);

        Marker marker = mAMap.addMarker(new MarkerOptions()

                .title("好好学习")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true));
        marker.setRotateAngle(90);// 设置marker旋转90度
        marker.setPositionByPixels(400, 400);
        marker.showInfoWindow();// 设置默认显示一个infowinfow

        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(34.341568, 108.940174));
        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");

        markerOption.draggable(true);
        markerOption.icon(
                // BitmapDescriptorFactory
                // .fromResource(R.drawable.location_marker)
                BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.mipmap.location_marker)));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        markerOption.setFlat(true);

        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        giflist.add(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
        giflist.add(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

    }


    /**
     * 文本框监听
     *
     * @param poi
     */
    @Override
    public void onPOIClick(Poi poi) {
        Log.d(TAG, "onPOIClick()");
        clearAll();
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(poi.getCoordinate());
        mPoiName.setText(poi.getName());
        mTv_latlng.setText(poi.getCoordinate().latitude + "+" + poi.getCoordinate().longitude);
//        whetherToShowDetailInfo(true);//等搜索完地址信息再显示

        markOptiopns.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.poi_marker_pressed)));
        mAMap.addMarker(markOptiopns);
        searchPoiByID(poi.getPoiId());

    }

    /**
     * 根据poi id搜索POI详情
     * <p>
     * 1、继承 OnPoiSearchListener 监听。
     * <p>
     * 2、构造 PoiSearch 对象，并设置监听。对于ID检索，query参数设置成 null。
     * <p>
     * 3、调用 PoiSearch 的 searchPOIIdAsyn(java.lang.String poiID) 方法发送请求。
     * <p>
     * 回调方法：onPoiItemSearched()
     *
     * @param poiId
     */
    public void searchPoiByID(String poiId) {
        poiSearch = new PoiSearch(mContext, null);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIIdAsyn(poiId);// 异步搜索
    }

    @Override
    public void onMapLoaded() {
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(AMapUtil.ZoomLevel));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onInfoWindowClick()");
//        getDrivingRoute(new LatLonPoint(marker.getPosition().latitude,marker.getPosition().longitude));
        //根据经纬度搜索地址信息
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude), 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        mTv_latlng.setText(marker.getPosition().latitude + "+" + marker.getPosition().longitude);
        geocoderSearch.getFromLocationAsyn(query);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.edt_search_mapFrag:
                    clearAll();
                    break;
                case R.id.btn_search_mapFrag://poiSearch
                    searchPoiByKey();
                    break;
                case R.id.ll_navigation_mapFrag://路径规划
//                    Log.e(TAG, "点击导航");

                    String address = mPoiAddress.getText().toString();
                    String latlng = mTv_latlng.getText().toString();
                    if (TextUtils.isEmpty(address)) {
                        Toast.makeText(mContext, "对不起，您的地址有误，请重新输入！", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (!TextUtils.isEmpty(latlng)) {
                        String[] split = latlng.split("\\+");
                        Log.i(TAG, "onClick():" + split.toString());
                        getDrivingRoute(new LatLonPoint(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
                    } else {
                        geoMarker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                        //构造 GeocodeSearch 对象，并设置监听。
                        geocoderSearch = new GeocodeSearch(mContext);
                        geocoderSearch.setOnGeocodeSearchListener(MapFragment.this);

                        //通过 GeocodeQuery(java.lang.String locationName, java.lang.String city) 设置查询参数，调用 GeocodeSearch 的 getFromLocationNameAsyn(GeocodeQuery geocodeQuery) 方法发起请求。
                        // name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
                        GeocodeQuery query = new GeocodeQuery(address, "");
                        geocoderSearch.getFromLocationNameAsyn(query);
//                    showDialog();
                    }
                    break;
            }
        }

    };


    /**
     * 获取路线
     * <p>
     *
     * @param latLonPoint 目的地坐标
     */
    private void getDrivingRoute(LatLonPoint latLonPoint) {
        //from
        lastKnownLocation = mLocationClient.getLastKnownLocation();
        mLatitude_current = lastKnownLocation.getLatitude();
        mLongitude_current = lastKnownLocation.getLongitude();
        LatLonPoint from = new LatLonPoint(mLatitude_current, mLongitude_current);

        //to
        LatLonPoint destination = new LatLonPoint(latLonPoint.getLatitude(), latLonPoint.getLongitude());

        //FromAndTo
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(from, destination);

        Intent intent = new Intent(mContext, DriveRouteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("fromAndTo", fromAndTo);
        bundle.putString("city", mLocationClient.getLastKnownLocation().getCity());
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /**
     * 用户输入关键字搜索
     */
    private void searchPoiByKey() {
//        showDialog();
        keyWord = mEdt_search.getText().toString().trim();
        currentPage = 0;
        if (TextUtils.isEmpty(keyWord)) {
            Toast.makeText(mContext, "请输入", Toast.LENGTH_SHORT).show();
//            dismissDialog();
            return;
        }

        query = new PoiSearch.Query(keyWord, "", mLocationClient.getLastKnownLocation().getCity());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(mContext, query);
            poiSearch.setOnPoiSearchListener(this);
            if (keyWord.contains("服务") || keyWord.contains("购物") || keyWord.contains("饮食") || keyWord.contains("美食")) {//设置搜索区域为以lp点为圆心，其周围5000米范围
                poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));
            }
            poiSearch.searchPOIAsyn();// 异步搜索
        } else {
            poiSearch = new PoiSearch(mContext, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * 周边搜索(高德POI)
     */
    private void searchPoiNeighborhood() {
        currentPage = 0;
        query = new PoiSearch.Query("服务", "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(mContext, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

//    /**
//     * 显示进度条对话框
//     */
//    private void showDialog() {
//        progDialog = new ProgressDialog(mContext);
//        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progDialog.setIndeterminate(false);
//        progDialog.setCancelable(true);
//        progDialog.setMessage("正在获取地址");
//        progDialog.show();
//    }

//    /**
//     * 隐藏进度条对话框
//     */
//    public void dismissDialog() {
//        if (progDialog != null) {
//            progDialog.dismiss();
//        }
//    }

    @Override
    public void onPoiSearched(PoiResult result, int code) {
//        dismissDialog();
        if (code == AMapException.CODE_AMAP_SUCCESS) {
            Log.d(TAG, "onPoiSearched(),pageCount:" + result.getPageCount() + ",pois:" + result.getPois() + ",SuggestionKeywords:" + result.getSearchSuggestionKeywords() + ",suggestionCitys:" + result.getSearchSuggestionCitys() + ",query():" + result.getQuery());
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
//                        if (current_marker != null) {
//                            resetlastmarker();
//                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay != null) {
                            poiOverlay.removeFromMap();
                        }
                        clearAll();
                        //创建PoiOverlay
                        poiOverlay = new MyPoiOverlay(mContext, mAMap, poiItems);
                        //添加marker到地图中
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

//                        for (PoiItem poiItem : poiItems) {
//                            Log.d(TAG, poiItem.getLatLonPoint().toString());
//                        }

//                        drawLocationCircle();

                        // 绘制边界
//                        mAMap.addCircle(new CircleOptions()
//                                .center(new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude())).radius(5000)
//                                .strokeColor(Color.BLUE)
//                                .fillColor(Color.argb(50, 1, 1, 1))
//                                .strokeWidth(2));

                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(mContext,
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil
                        .show(mContext, R.string.no_result);
            }
        } else {
//            ToastUtil.showerror(mContext, code);
        }
    }


    @Override
    public void onPoiItemSearched(PoiItem item, int i) {
        Log.d(TAG, "onPoiItemSearched()" + ",getTitle:" + item.getTitle() + ",getSnippet:" + item.getSnippet() + "，distance：" + item.getDistance() + ",getAdCode:" + item.getAdCode());

        mPoiName.setText(item.toString());
        mPoiAddress.setText(item.getSnippet());
        mTv_latlng.setText(item.getLatLonPoint().getLatitude() + "+" + item.getLatLonPoint().getLongitude());

        whetherToShowDetailInfo(true);
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(mContext, infomation);
    }

    /**
     * 清除所有覆盖物，包括定位的小蓝点
     */
    private void clearAll() {
        mAMap.clear();
        drawLocationCircle();
        whetherToShowDetailInfo(false);
    }

    /**
     * 绘制定位小蓝点
     */
    private void drawLocationCircle() {
        mAMap.addMarker(new MarkerOptions()
                .anchor(0.2f, 0.2f)
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(), R.mipmap.point4)))
                .position(new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude())));
    }

    /**
     * 将之前被点击的marker置为原来的状态
     */
    private void resetlastmarker() {
        if (current_marker == null) {
            return;
        }
        int index = poiOverlay.getPoiIndex(current_marker);
        if (index < 10) {
            current_marker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        } else {
            current_marker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.mipmap.poi_marker_pressed)));
        }
        current_marker = null;
    }


//    private int[] markers = {
//            R.drawable.default_icon,
//            R.drawable.default_icon,
//            R.drawable.default_icon,
//            R.drawable.default_icon,
//            R.drawable.default_icon,
//            R.drawable.default_icon,
//            R.drawable.default_icon,
//            R.drawable.default_icon,
//            R.drawable.default_icon,
//            R.drawable.default_icon
//    };


    private int[] markers = {
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_2,
            R.mipmap.poi_marker_3,
            R.mipmap.poi_marker_4,
            R.mipmap.poi_marker_5,
            R.mipmap.poi_marker_6,
            R.mipmap.poi_marker_7,
            R.mipmap.poi_marker_8,
            R.mipmap.poi_marker_9,
            R.mipmap.poi_marker_10
    };

    /**
     * 是否显示详情
     *
     * @param isToShow
     */
    private void whetherToShowDetailInfo(boolean isToShow) {
//        mIsShown = isToShow;
        if (isToShow) {
            mPoiDetailView.setVisibility(View.VISIBLE);

        } else {
            mPoiDetailView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        //解析result获取地址描述信息
//        dismissDialog();
//        Log.i(TAG, "onRegeocodeSearched(),rCode:" + i);
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (regeocodeResult != null) {
                RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                String address = regeocodeAddress.getFormatAddress();
                Log.i(TAG, "onRegeocodeSearched()," + regeocodeAddress.getTownship() + regeocodeAddress.getBusinessAreas());
                mPoiName.setText(regeocodeAddress.getNeighborhood());
                mPoiAddress.setText(address);
                whetherToShowDetailInfo(true);
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
//        dismissDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
                geoMarker.setPosition(AMapUtil.convertToLatLng(address
                        .getLatLonPoint()));
                String addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
                        + address.getFormatAddress() + "\n如果不对，请试试带上城市的名字！";

                getDrivingRoute(address.getLatLonPoint());

                ToastUtil.show(mContext, addressName);
            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(mContext, rCode);
        }
    }

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mAMap.getProjection();
        final LatLng markerLatlng = marker.getPosition();
        Point markerPoint = proj.toScreenLocation(markerLatlng);
        markerPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(markerPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * markerLatlng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * markerLatlng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
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

}
