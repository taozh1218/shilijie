package com.jiaohe.sakamichi.xinzhiying.ui.overlay;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.jiaohe.sakamichi.xinzhiying.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义PoiOverlay
 * <p>
 * Created by DIY on 2017/2/23.
 */

public class MyPoiOverlay {

    private Context mContext;
    private AMap amap;
    private List<PoiItem> pois;
    private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();

    public MyPoiOverlay(Context context, AMap amap, List<PoiItem> pois) {
        this.amap = amap;
        this.pois = pois;
        mContext = context;
    }

    /**
     * 添加Marker到地图中。
     *
     * @since V2.1.0
     */
    public void addToMap() {
        for (int i = 0; i < pois.size(); i++) {
            Marker marker = amap.addMarker(getMarkerOptions(i));
            PoiItem item = pois.get(i);
            marker.setObject(item);
            mPoiMarks.add(marker);
        }
    }

    /**
     * 去掉PoiOverlay上所有的Marker。
     *
     * @since V2.1.0
     */
    public void removeFromMap() {
        for (Marker mark : mPoiMarks) {
            mark.remove();
        }
    }

    /**
     * 移动镜头到当前的视角。
     *
     * @since V2.1.0
     */
    public void zoomToSpan() {
        if (pois != null && pois.size() > 0) {
            if (amap == null)
                return;
            LatLngBounds bounds = getLatLngBounds();
            amap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }

    private LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < pois.size(); i++) {
            b.include(new LatLng(pois.get(i).getLatLonPoint().getLatitude(),
                    pois.get(i).getLatLonPoint().getLongitude()));
        }
        return b.build();
    }

    private MarkerOptions getMarkerOptions(int index) {
        return new MarkerOptions()
                .position(
                        new LatLng(pois.get(index).getLatLonPoint()
                                .getLatitude(), pois.get(index)
                                .getLatLonPoint().getLongitude()))
                .title(getTitle(index)).snippet(getSnippet(index))
                .icon(getBitmapDescriptor(index));
    }

    protected String getTitle(int index) {
        return pois.get(index).getTitle();
    }

    protected String getSnippet(int index) {
        return pois.get(index).getSnippet();
    }

    /**
     * 从marker中得到poi在list的位置。
     *
     * @param marker 一个标记的对象。
     * @return 返回该marker对应的poi在list的位置。
     * @since V2.1.0
     */
    public int getPoiIndex(Marker marker) {
        for (int i = 0; i < mPoiMarks.size(); i++) {
            if (mPoiMarks.get(i).equals(marker)) {

                return i;
            }
        }
        return -1;
    }

    /**
     * 返回第index的poi的信息。
     *
     * @param index 第几个poi。
     * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
     * @since V2.1.0
     */
    public PoiItem getPoiItem(int index) {
        if (index < 0 || index >= pois.size()) {
            return null;
        }
        return pois.get(index);
    }

    protected BitmapDescriptor getBitmapDescriptor(int arg0) {
        if (arg0 < 10) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), markers[arg0]));
            return icon;
        } else {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.poi_marker_pressed));
            return icon;
        }
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
}
