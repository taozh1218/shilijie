package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.RouteSearch;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.adapter.MyInfoWindowAdapter;
import com.jiaohe.sakamichi.xinzhiying.ui.overlay.MyPoiOverlay;
import com.jiaohe.sakamichi.xinzhiying.util.AMapUtil;
import com.jiaohe.sakamichi.xinzhiying.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class DemoInfoWindowAct extends AppCompatActivity implements AMap.OnMapClickListener, AMap.OnMarkerClickListener, LocationSource, AMapLocationListener, AMap.OnPOIClickListener, AMap.OnMapLoadedListener, AMap.OnInfoWindowClickListener, View.OnClickListener, PoiSearch.OnPoiSearchListener, GeocodeSearch.OnGeocodeSearchListener {

    private static final String TAG = "DemoInfoWindowAct";
    private AMap mAMap;
    private UiSettings mUiSettings;
    private MapView mMapView;
    private MyInfoWindowAdapter adapter;
    private Marker oldMarker;//之前的marker
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation lastKnownLocation;
    private LatLonPoint lp;
    private double mLatitude_current;
    private double mLongitude_current;
    private MarkerOptions markerOption;
    private GeocodeSearch geocoderSearch;

    private ProgressDialog progDialog;
    private EditText mEdt_search;
    //底部详情
    private RelativeLayout mPoiDetailView;
    private TextView mPoiName;
    private TextView mPoiAddress;
    private TextView mPoiInfo;
    private TextView mTv_latlng;
    private boolean mIsShown;//标记底部是否在显示
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

    private RouteSearch mRouteSearch;
    private final int ROUTE_TYPE_WALK = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_window);

        mMapView = (MapView) findViewById(R.id.map_InfoWindowAct);
        mMapView.onCreate(savedInstanceState);

        init();
    }

    public void init() {
        initMap();
        //init View
        mEdt_search = (EditText) findViewById(R.id.edt_InfoWindowAct);
        Button btn = (Button) findViewById(R.id.btn_InfoWindowAct);

        mPoiDetailView = (RelativeLayout) findViewById(R.id.rl_detail_InfoWindowAct);
        mPoiName = (TextView) findViewById(R.id.poi_name_InfoWindowAct);
        mPoiAddress = (TextView) findViewById(R.id.poi_address_InfoWindowAct);
        mPoiInfo = (TextView) findViewById(R.id.poi_info_InfoWindowAct);
        mTv_latlng = (TextView) findViewById(R.id.tv_latlng_InfoWindowAct);

        btn.setOnClickListener(this);
        mPoiDetailView.setOnClickListener(this);


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
        mAMap.setOnMarkerClickListener(this);
        mAMap.setOnInfoWindowClickListener(this);
        mAMap.setOnPOIClickListener(this);
        mAMap.setOnMapLoadedListener(this);
//        geocoderSearch = new GeocodeSearch(this);
//        geocoderSearch.setOnGeocodeSearchListener(this);

        //自定义InfoWindow
        adapter = new MyInfoWindowAdapter(this);
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
    }

    //地图的点击事件
    @Override
    public void onMapClick(LatLng latLng) {
        //点击地图上没marker 的地方，隐藏infoWindow
        if (oldMarker != null) {
            oldMarker.hideInfoWindow();
            oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.default_icon));
//            clearAll();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick()");
        oldMarker = marker;
//        if (oldMarker != null) {
//            oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.poi_marker_pressed));
//        }
//        oldMarker = marker;
//        marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.poi_marker_pressed));
        return false; //返回 “false”，除定义的操作之外，默认操作也将会被执行
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
     * 点击定位按钮，也会触发此回调
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        Log.d(TAG, "active()");
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(AMapUtil.ZoomLevel));
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(getApplicationContext());
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

                searchPoiNeighborhood();

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
     * 在地图上添加marker
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

        markerOption = new MarkerOptions();
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


    }

    @Override
    public void onMapLoaded() {
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(AMapUtil.ZoomLevel));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "onInfoWindowClick()");
        getDrivingRoute(new LatLonPoint(marker.getPosition().latitude,marker.getPosition().longitude));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_InfoWindowAct://poiSearch
                searchPoiByKey();
                break;
            case R.id.rl_detail_InfoWindowAct://TODO 其他路径规划，在DriveRouteActivity设置
                String address = mPoiAddress.getText().toString();
                String latlng = mTv_latlng.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getApplicationContext(), "对不起，您的地址有误，请重新输入！", Toast.LENGTH_SHORT).show();
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
                    geocoderSearch = new GeocodeSearch(getApplicationContext());
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

        Intent intent = new Intent(getApplicationContext(), DriveRouteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("fromAndTo", fromAndTo);
        bundle.putString("city",mLocationClient.getLastKnownLocation().getCity());
        intent.putExtras(bundle);
        startActivity(intent);
    }




    private void searchPoiByKey() {
        showDialog();
        keyWord = mEdt_search.getText().toString().trim();
        currentPage = 0;
        if (TextUtils.isEmpty(keyWord)) {
            Toast.makeText(getApplicationContext(), "请输入", Toast.LENGTH_SHORT).show();
            dismissDialog();
            return;
        }

        query = new PoiSearch.Query(keyWord, "", mLocationClient.getLastKnownLocation().getCity());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(getApplicationContext(), query);
            poiSearch.setOnPoiSearchListener(this);
            if (keyWord.contains("服务") || keyWord.contains("购物") || keyWord.contains("饮食") || keyWord.contains("美食")) {//设置搜索区域为以lp点为圆心，其周围5000米范围
                poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));
            }
            poiSearch.searchPOIAsyn();// 异步搜索
        } else {
            poiSearch = new PoiSearch(getApplicationContext(), query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * 周边搜索
     */
    private void searchPoiNeighborhood() {
        currentPage = 0;
        query = new PoiSearch.Query("服务", "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * 显示进度条对话框
     */
    private void showDialog() {
        progDialog = new ProgressDialog(this);
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
//                        if (oldMarker != null) {
//                            resetlastmarker();
//                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay != null) {
                            poiOverlay.removeFromMap();
                        }
                        clearAll();
                        //创建PoiOverlay
                        poiOverlay = new MyPoiOverlay(getApplicationContext(), mAMap, poiItems);
                        //添加marker到地图中
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

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
                        ToastUtil.show(getApplicationContext(),
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil
                        .show(getApplicationContext(), R.string.no_result);
            }
        } else {
            ToastUtil.showerror(getApplicationContext(), code);
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
        ToastUtil.show(getApplicationContext(), infomation);
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
        if (oldMarker == null) {
            return;
        }
        int index = poiOverlay.getPoiIndex(oldMarker);
        if (index < 10) {
            oldMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        } else {
            oldMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.mipmap.marker_other_highlight)));
        }
        oldMarker = null;
    }


    private int[] markers = {
            R.drawable.default_icon,
            R.drawable.default_icon,
            R.drawable.default_icon,
            R.drawable.default_icon,
            R.drawable.default_icon,
            R.drawable.default_icon,
            R.drawable.default_icon,
            R.drawable.default_icon,
            R.drawable.default_icon,
            R.drawable.default_icon
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

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

//    /**
//     * 自定义infowinfow窗口
//     */
//    public void render(Marker marker, View view) {
//
//        String title = marker.getTitle();
//        TextView titleUi = ((TextView) view.findViewById(R.id.title));
//        if (title != null) {
//            SpannableString titleText = new SpannableString(title);
//            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
//                    titleText.length(), 0);
//            titleUi.setTextSize(15);
//            titleUi.setText(titleText);
//
//        } else {
//            titleUi.setText("");
//        }
//        String snippet = marker.getSnippet();
//        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
//        if (snippet != null) {
//            SpannableString snippetText = new SpannableString(snippet);
//            snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
//                    snippetText.length(), 0);
//            snippetUi.setTextSize(20);
//            snippetUi.setText(snippetText);
//        } else {
//            snippetUi.setText("");
//        }
//    }

}
