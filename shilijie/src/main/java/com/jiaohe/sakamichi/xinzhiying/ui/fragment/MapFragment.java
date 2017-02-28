package com.jiaohe.sakamichi.xinzhiying.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
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
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.adapter.MyInfoWindowAdapter;
import com.jiaohe.sakamichi.xinzhiying.ui.acitivity.DriveRouteActivity;
import com.jiaohe.sakamichi.xinzhiying.ui.overlay.MyPoiOverlay;
import com.jiaohe.sakamichi.xinzhiying.ui.view.LoadingLayout;
import com.jiaohe.sakamichi.xinzhiying.util.AMapUtil;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends BaseFragment implements View.OnClickListener, AMap.OnPOIClickListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener, LocationSource, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, AMap.OnMapLongClickListener, AMap.OnMapLoadedListener, GeocodeSearch.OnGeocodeSearchListener, PoiSearch.OnPoiSearchListener, AMapLocationListener {

    private static final String TAG = "MapFragment";

    public Context mContext;
    private static MapFragment fragment = null;
    private View mapLayout;
    //    @ViewInject(R.id.fragment_map)
    private MapView mMapView;
    private AMap mAMap;
    private UiSettings mUiSettings;
    private LatLonPoint lp;
    private AMapLocationClient mLocationClient;

    private EditText mEdt;
    private RelativeLayout mPoiDetailView;
    private TextView mPoiName;
    private TextView mPoiAddress;
    private TextView mPoiInfo;
    private TextView mTv_latlng;

    //poiSearch参数
    private String keyWord;
    private int currentPage;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private ProgressDialog progDialog;

    //poiSearch result
    private ArrayList<PoiItem> mPoiItems;
    private PoiResult poiResult;
    private ArrayList<PoiItem> poiItems;
    private Marker mlastMarker;
    private MyPoiOverlay poiOverlay;
    private Marker detailMarker;
    private Marker geoMarker;
    private boolean mIsShown = false;
    private GeocodeSearch geocoderSearch;
    private String addressName;
    private double mLongitude_current;
    private double mLatitude_current;
    private RouteSearch routeSearch;
    private Location lastKnownLocation;
    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;
    //自定义infoWindow的adapter
    private MyInfoWindowAdapter adapter;

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
        // Inflate the layout for this fragment
        if (mapLayout == null) {
//            mapLayout = inflater.inflate(R.layout.fragment_map, null);
//            ViewUtils.inject(this, mapLayout);
            mapLayout = inflater.inflate(R.layout.fragment_map, container, false);
            mMapView = (MapView) mapLayout.findViewById(R.id.map_poiClickAct);//findMap
            mMapView.onCreate(savedInstanceState);//创建地图，必须重写
            init();
            initView();
            if (mAMap == null) {
                mAMap = mMapView.getMap();
            } else {
                if (mapLayout.getParent() != null) {
                    Log.d(TAG, "onCreateView,aMap!=null&mapLayout.getParent()!=null");
                    ((ViewGroup) mapLayout.getParent()).removeView(mapLayout);
                }
            }
        }
        return mapLayout;
    }


    private void init() {
        //获取aMap
        if (mAMap == null) {
            Log.d(TAG, "init(),aMap==null");
            mAMap = mMapView.getMap();
            mAMap.setLocationSource(this);// 设置定位监听
            mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

            mUiSettings = mAMap.getUiSettings();
            mUiSettings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            mUiSettings.setCompassEnabled(true);//指南针
            mUiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示
//            TODO 判断是否有网，无网则进行GPS定位
            //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);

            mAMap.setOnPOIClickListener(this);
            mAMap.setOnMapClickListener(this);
            mAMap.setOnMarkerClickListener(this);
            mAMap.setOnInfoWindowClickListener(this);
            mAMap.setInfoWindowAdapter(this);
            mAMap.setOnMapLongClickListener(this);
            mAMap.setOnMapLoadedListener(this);
            geocoderSearch = new GeocodeSearch(getActivity());
            geocoderSearch.setOnGeocodeSearchListener(this);
            //TODO 自定义InfoWindow
//            adapter = new MyInfoWindowAdapter(getActivity().getApplicationContext());
//            mAMap.setInfoWindowAdapter(adapter);


            lp = new LatLonPoint(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude());
//            locationMarker = mAMap.addMarker(new MarkerOptions()
//                    .anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory
//                            .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.point4)))
//                    .position(new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude())));//显示当前坐标的圆点
//            locationMarker.showInfoWindow();
        } else {
            Log.d(TAG, "init(),aMap!=null");
            mUiSettings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        }

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
            mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
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
        } else {
            //清除所有标记
            if (mPoiDetailView.getVisibility() == View.VISIBLE) {
                mPoiDetailView.setVisibility(View.GONE);
            }
            resetlastmarker();
            //清理之前搜索结果的marker
            if (poiOverlay != null) {
                poiOverlay.removeFromMap();
            }
            clearAll();
        }
    }

    /**
     * 定位成功的回调
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
//        mAMap.moveCamera(CameraUpdateFactory.zoomTo(AMapUtil.ZoomLevel));
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                Log.d(TAG, "onLocationChanged()&aMapLocation!=null");
                //重置当前坐标
                lp = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mLatitude_current = aMapLocation.getLatitude();
                mLongitude_current = aMapLocation.getLongitude();

                mAMap.moveCamera(CameraUpdateFactory.zoomTo(AMapUtil.ZoomLevel));
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                Log.d(TAG, "onLocationChanged(),city:" + aMapLocation.getCity() +
                        "，province：" + aMapLocation.getProvince() +
                        ",country:" + aMapLocation.getCountry() +
                        ",getLocationDetail:" + aMapLocation.getLocationDetail()
                );
            }
        }
    }


    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }


    private void initView() {
        mEdt = (EditText) mapLayout.findViewById(R.id.edt_poiClickAct);
        Button btn = (Button) mapLayout.findViewById(R.id.btn_poiClickAct);

        mPoiDetailView = (RelativeLayout) mapLayout.findViewById(R.id.rl_detail_poiClickAct);
        mPoiName = (TextView) mapLayout.findViewById(R.id.poi_name_poiClickAct);
        mPoiAddress = (TextView) mapLayout.findViewById(R.id.poi_address_poiClickAct);
        mPoiInfo = (TextView) mapLayout.findViewById(R.id.poi_info_poiClickAct);
        mTv_latlng = (TextView) mapLayout.findViewById(R.id.tv_latlng_poiClickAct);

        btn.setOnClickListener(this);
        mPoiDetailView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_poiClickAct://poiSearch
                searchPoiByKey();
                break;
            case R.id.rl_detail_poiClickAct://TODO 其他路径规划，在DriveRouteActivity设置
                String address = mPoiAddress.getText().toString();
                String latlng = mTv_latlng.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getActivity().getApplicationContext(), "对不起，您的地址有误，请重新输入！", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!TextUtils.isEmpty(latlng)) {
                    //
                    String[] split = latlng.split("\\+");
                    Log.i(TAG, "onClick():" + split.toString());
                    getDrivingRoute(new LatLonPoint(Double.parseDouble(split[0]), Double.parseDouble(split[1])));
                } else {
                    geoMarker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    //构造 GeocodeSearch 对象，并设置监听。
                    geocoderSearch = new GeocodeSearch(getActivity().getApplicationContext());
                    geocoderSearch.setOnGeocodeSearchListener(this);

                    //通过 GeocodeQuery(java.lang.String locationName, java.lang.String city) 设置查询参数，调用 GeocodeSearch 的 getFromLocationNameAsyn(GeocodeQuery geocodeQuery) 方法发起请求。
                    // name表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode
                    GeocodeQuery query = new GeocodeQuery(address, "");
                    geocoderSearch.getFromLocationNameAsyn(query);
//                    showDialog();
                }
                break;
        }

    }


    /**
     * 开始进行poi搜索
     * <p>
     * 回调方法：onPoiSearched()
     */
    protected void searchPoiByKey() {
//        showDialog();
        keyWord = mEdt.getText().toString().trim();
        currentPage = 0;
        if (TextUtils.isEmpty(keyWord)) {
            Toast.makeText(getActivity().getApplicationContext(), "请输入", Toast.LENGTH_SHORT).show();
            return;
        }
        query = new PoiSearch.Query(keyWord, "", mLocationClient.getLastKnownLocation().getCity());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(getActivity().getApplicationContext(), query);
            poiSearch.setOnPoiSearchListener(this);
//            poiSearch.setBound(new SearchBound(lp, 5000, true)); // TODO 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        } else {
            poiSearch = new PoiSearch(getActivity().getApplicationContext(), query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * PoiSearch result
     *
     * @param result
     * @param code
     */
    @Override
    public void onPoiSearched(PoiResult result, int code) {
        dismissDialog();
        if (code == AMapException.CODE_AMAP_SUCCESS) {
            Log.e(TAG, "onPoiSearched(),pageCount:" + result.getPageCount() + ",pois:" + result.getPois() + ",SuggestionKeywords:" + result.getSearchSuggestionKeywords() + ",suggestionCitys:" + result.getSearchSuggestionCitys() + ",query():" + result.getQuery());
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
                        if (mlastMarker != null) {
                            resetlastmarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay != null) {
                            poiOverlay.removeFromMap();
                        }
                        clearAll();
                        //创建PoiOverlay
                        poiOverlay = new MyPoiOverlay(getActivity().getApplicationContext(), mAMap, poiItems);
                        //添加marker到地图中
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        drawLocationCircle();

                        //绘制边界
                        mAMap.addCircle(new CircleOptions()
                                .center(new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude())).radius(5000)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 1, 1, 1))
                                .strokeWidth(2));

                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(getActivity().getApplicationContext(),
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil
                        .show(getActivity().getApplicationContext(), R.string.no_result);
            }
        } else {
            ToastUtil.showerror(getActivity().getApplicationContext(), code);
        }
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
        ToastUtil.show(getActivity().getApplicationContext(), infomation);

    }

    /**
     * 4、通过回调接口 onPoiItemSearched 解析返回的结果。由于是检索具体的某一个POI，直接回调该POI对象 PoiItem。
     *
     * @param item
     * @param i
     */
    @Override
    public void onPoiItemSearched(PoiItem item, int i) {
        Log.d(TAG, "onPoiItemSearched()" + ",getTitle:" + item.getTitle() + ",getSnippet:" + item.getSnippet() + "，distance：" + item.getDistance() + ",getAdCode:" + item.getAdCode());

        mPoiName.setText(item.toString());
        mPoiAddress.setText(item.getSnippet());
        mTv_latlng.setText(item.getLatLonPoint().getLatitude() + "+" + item.getLatLonPoint().getLongitude());

        whetherToShowDetailInfo(true);

    }


    /**
     * 显示进度条对话框
     */
    private void showDialog() {
        progDialog = new ProgressDialog(getActivity().getApplicationContext());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
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
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }


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

        Intent intent = new Intent(getActivity().getApplicationContext(), DriveRouteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("fromAndTo", fromAndTo);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * poi clickListener
     * <p>
     * 点击POI时，为该点设置marker，然后显示detail（等获取到地址信息再显示）
     *
     * @param poi
     */
    @Override
    public void onPOIClick(Poi poi) {
        Log.i(TAG, "onPOIClick(),PoiId:" + poi.getPoiId() + ",name:" + poi.getName());
        clearAll();
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(poi.getCoordinate());
//        TextView textView = new TextView(getApplicationContext());
//        textView.setPadding(5, 5, 5, 5);
//        textView.setText("到" + poi.getName() + "去");
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(Color.BLACK);
//        textView.setBackgroundResource(R.mipmap.custom_info_bubble);
//        markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
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
        poiSearch = new PoiSearch(getActivity().getApplicationContext(), null);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIIdAsyn(poiId);// 异步搜索
    }

    @Override
    public void onMapClick(LatLng latLng) {
//        Log.d(TAG, "onMapClick()," + latLng.toString() + ",latLng.describeContents():" + latLng.describeContents());
        clearAll();
        mPoiDetailView.setVisibility(View.GONE);
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
        Log.d(TAG, "onMarkerClick()," + marker.toString());
        if (marker.getObject() != null) {

            whetherToShowDetailInfo(true);
            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.mipmap.poi_marker_pressed)));
                setPoiItemDisplayContent(mCurrentPoi);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }
        return false;
    }

    /**
     * 设置item信息
     *
     * @param mCurrentPoi
     */
    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet() + mCurrentPoi.getDistance());

        mTv_latlng.setText(mCurrentPoi.getLatLonPoint().getLatitude() + "+" + mCurrentPoi.getLatLonPoint().getLongitude());

    }

    /**
     * 将之前被点击的marker置为原来的状态
     */
    private void resetlastmarker() {
        if (mlastMarker == null) {
            return;
        }
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        } else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.mipmap.marker_other_highlight)));
        }
        mlastMarker = null;

    }

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
        mIsShown = isToShow;
        if (isToShow) {
            mPoiDetailView.setVisibility(View.VISIBLE);

        } else {
            mPoiDetailView.setVisibility(View.GONE);

        }
    }


    /**
     * 清除所有覆盖物，包括定位的小蓝点
     */
    private void clearAll() {
        mAMap.clear();
        drawLocationCircle();
    }

    /**
     * 绘制定位小蓝点
     */
    private void drawLocationCircle() {
        mAMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(), R.mipmap.point4)))
                .position(new LatLng(mLocationClient.getLastKnownLocation().getLatitude(), mLocationClient.getLastKnownLocation().getLongitude())));
    }

    private void addMarkerToMap(LatLng latLng, String title, String snippet) {
        mAMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.poi_marker_pressed))
        );
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onInfoWindowClick()," + marker.toString());
    }

    /**
     * 自定义infowindow窗口的infowindow事件回调
     *
     * @param marker
     * @return
     */
    @Override
    public View getInfoWindow(Marker marker) {
        Log.d(TAG, "getInfoWindow()" + marker.getId());

//        View infoWindow = getActivity().getLayoutInflater().inflate(
//                R.layout.layout_infowindow, null);
//        render(marker, infoWindow);
//        return infoWindow;

        return null;
    }

    /**
     * 自定义infoWindow窗口的infoContents事件回调
     *
     * @param marker
     * @return
     */
    @Override
    public View getInfoContents(Marker marker) {
        Log.d(TAG, "getInfoContents()" + marker.getId());
//        View infoContent = getActivity().getLayoutInflater().inflate(
//                R.layout.layout_infowindow, null);
//        render(marker, infoContent);
//        return infoContent;
        return null;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {
        LinearLayout mRoot_view = (LinearLayout) view.findViewById(R.id.ll_infoWindow);
        ImageView img_avatar = (ImageView) view.findViewById(R.id.img_avatar_infoWindow);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name_infoWindow);
        TextView tv_phoneNo = (TextView) view.findViewById(R.id.tv_phoneNo_infoWindow);
        ImageView img_verification = (ImageView) view.findViewById(R.id.img_verification_infoWindow);
        EditText edt_sign = (EditText) view.findViewById(R.id.tv_signature_infoWindow);
//        mEdt.setKeyListener(null);
        TextView tv_address = (TextView) view.findViewById(R.id.tv_address_infoWindow);

//        img_avatar.setImageBitmap();//TODO 设置用户头像
        tv_name.setText(SPUtils.getString(getActivity().getApplicationContext(), "nickname", ""));//用户名
        tv_phoneNo.setText(SPUtils.getString(getActivity().getApplicationContext(), "phone", ""));//用户手机号
//        tv_name.setText(String.format(mContext.getString(R.string.agent_addr), marker.getSnippet()));
        tv_address.setText(marker.getPosition().latitude + "+" + marker.getPosition().longitude);


    }


    /**
     * map Load finish
     */
    @Override
    public void onMapLoaded() {
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(AMapUtil.ZoomLevel));
    }

    /**
     * 通过经纬度获取地址详情
     *
     * @param regeocodeResult
     * @param i
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        //解析result获取地址描述信息
        dismissDialog();
        Log.i(TAG, "onRegeocodeSearched(),i" + i);
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
        dismissDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
                geoMarker.setPosition(AMapUtil.convertToLatLng(address
                        .getLatLonPoint()));
                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
                        + address.getFormatAddress() + "\n如果不对，请试试带上城市的名字！";

                getDrivingRoute(address.getLatLonPoint());


                ToastUtil.show(getActivity().getApplicationContext(), addressName);
            } else {
                ToastUtil.show(getActivity().getApplicationContext(), R.string.no_result);
            }
        } else {
            ToastUtil.showerror(getActivity().getApplicationContext(), rCode);
        }
    }


}
