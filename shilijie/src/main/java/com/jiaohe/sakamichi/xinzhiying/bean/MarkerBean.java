package com.jiaohe.sakamichi.xinzhiying.bean;

import android.graphics.Bitmap;

/**
 * marker自定义属性。
 *
 * Created by taozh on 2017/3/1.
 */

public class MarkerBean {

    Bitmap bitmap;
    String signature;
    String phone;
    String name;
    //用户详情页
    String title;
    String snippet;

    public MarkerBean() {
    }

    public MarkerBean(Bitmap bitmap, String signature, String phone, String name, String title, String snippet) {
        this.bitmap = bitmap;
        this.signature = signature;
        this.phone = phone;
        this.name = name;
        this.title = title;
        this.snippet = snippet;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public String toString() {
        return "MarkerBean{" +
                "bitmap=" + bitmap +
                ", signature='" + signature + '\'' +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", snippet='" + snippet + '\'' +
                '}';
    }
}
