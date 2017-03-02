package com.jiaohe.sakamichi.xinzhiying.bean;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.location.AMapLocation;

/**
 * Created by DIY on 2017/2/28.
 */

public class MyLocation extends AMapLocation implements Parcelable {
    public MyLocation(String s) {
        super(s);
    }

    public MyLocation(Location location) {
        super(location);
    }

//    protected MyLocation(Parcel in) {
//    }

//    public static final Creator<MyLocation> CREATOR = new Creator<MyLocation>() {
//        @Override
//        public MyLocation createFromParcel(Parcel in) {
//            return new MyLocation(in);
//        }
//
//        @Override
//        public MyLocation[] newArray(int size) {
//            return new MyLocation[size];
//        }
//    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
//    public MyLocation(String s) {
//        super(s);
//    }
//
//    public MyLocation(Location location) {
//        super(location);
//    }

//    @Override
//    public String getCity() {
//        return super.getCity();
//    }

}
