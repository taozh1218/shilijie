package com.jiaohe.sakamichi.xinzhiying.bean;

import android.graphics.Bitmap;

/**
 * Created by DIY on 2017/2/28.
 */

public class UserLocationBean {

    String id;
    String userid;
    String latitude;
    String longitude;
    String datetime;
    String type;
    String state;
    UserInfo userinfo;
    Bitmap mBitmap;

    public UserLocationBean() {
    }

    public UserLocationBean(String id, String userid, String latitude, String longitude, String datetime, String type, String state, UserInfo userinfo, Bitmap bitmap) {
        this.id = id;
        this.userid = userid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.datetime = datetime;
        this.type = type;
        this.state = state;
        this.userinfo = userinfo;
        mBitmap = bitmap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UserInfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserInfo userinfo) {
        this.userinfo = userinfo;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    @Override
    public String toString() {
        return "UserLocationBean{" +
                "id='" + id + '\'' +
                ", userid='" + userid + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", datetime='" + datetime + '\'' +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                ", userinfo=" + userinfo +
                ", mBitmap=" + mBitmap +
                '}';
    }
}
