package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.bean.UserInfoBean;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.ui.fragment.FragmentFactory;
import com.jiaohe.sakamichi.xinzhiying.ui.view.AvatarImageView;
import com.jiaohe.sakamichi.xinzhiying.ui.view.NoScrollViewPager;
import com.jiaohe.sakamichi.xinzhiying.util.RequestUtils;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UriUtils;
import com.jiaohe.sakamichi.xinzhiying.util.VolleyInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout mDl_root;
    private AvatarImageView mIv_icon;
    private ImageView mIv_camera;
    private NoScrollViewPager mVp_content;
    private ImageButton mIb_menu;
    private String phone;
    private String token;
    private final String path = Environment.getExternalStorageDirectory() + "/crop_icon.jpg";
    private ImageView mIv_slide_icon;
    private EditText mEt_search;
    private ImageView mIv_qr;
    private ImageView mIv_wei;
    private ImageView mIv_dian;
    private ImageView mIv_qi;
    private ImageView mIv_search;
    private ImageView mIv_scan;
    private TextView mTv_id;
    private TextView mTv_phone;
    private TextView mTv_fame;
    private TextView mTv_sign;
    private TextView mTv_weather;
    private TextView mTv_temperature;
    private TextView mTv_wind;
    private TextView mTv_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        UIUtils.initStateBar(MainActivity.this); //设置透明状态栏
        initView(); //初始化MainActivity中控件
        initSlideMenuView(); //初始化侧边栏中控件

        initUserIcon();
        initUserMsg();
    }

    private void initData() {
        phone = SPUtils.getString(this, "phone", "");
        token = SPUtils.getString(this, "token", "");

    }

    private void initUserMsg() {
        Boolean isUpload = SPUtils.getBoolean(this, "isUpload", false);
        if (isUpload) {
            System.out.println("走这了");
            String nickname = SPUtils.getString(this, "nickname", "娇禾生物");
            String sign = SPUtils.getString(this, "sign", "有人的地方就有江湖");
            mTv_sign.setText(sign);
            mTv_id.setText(nickname);
        } else {
            getInterUserInfo();
        }

    }

    private void initUserIcon() {
        Boolean isCache = SPUtils.getBoolean(this, "isCache", false);

        if (isCache) {
            Uri uriFromFilePath = UriUtils.getUriFromFilePath(path);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriFromFilePath);
                mIv_icon.setImageBitmap(bitmap);
                mIv_slide_icon.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            getInterImage();
        }

    }

    private void initSlideMenuView() {
        final RadioGroup rg_navi = (RadioGroup) findViewById(R.id.rg_navi);
        mEt_search = (EditText) findViewById(R.id.et_search);
        mIv_slide_icon = (ImageView) findViewById(R.id.iv_slide_icon);
        mIv_qr = (ImageView) findViewById(R.id.iv_qr);
        mIv_wei = (ImageView) findViewById(R.id.iv_wei);
        mIv_dian = (ImageView) findViewById(R.id.iv_dian);
        mIv_qi = (ImageView) findViewById(R.id.iv_qi);
        mIv_search = (ImageView) findViewById(R.id.iv_search);
        mIv_scan = (ImageView) findViewById(R.id.iv_scan);
        mTv_id = (TextView) findViewById(R.id.tv_id);
        mTv_phone = (TextView) findViewById(R.id.tv_phone);
        mTv_fame = (TextView) findViewById(R.id.tv_fame);
        mTv_sign = (TextView) findViewById(R.id.tv_sign);
        mTv_weather = (TextView) findViewById(R.id.tv_weather);
        mTv_temperature = (TextView) findViewById(R.id.tv_temperature);
        mTv_wind = (TextView) findViewById(R.id.tv_wind);
        mTv_location = (TextView) findViewById(R.id.tv_location);
        LinearLayout ll_address = (LinearLayout) findViewById(R.id.ll_address);
        LinearLayout authentication = (LinearLayout) findViewById(R.id.ll_authentication);
        LinearLayout favourite = (LinearLayout) findViewById(R.id.ll_favourite);
        LinearLayout market = (LinearLayout) findViewById(R.id.ll_market);
        LinearLayout order = (LinearLayout) findViewById(R.id.ll_order);
        LinearLayout wallet = (LinearLayout) findViewById(R.id.ll_wallet);
        LinearLayout config = (LinearLayout) findViewById(R.id.ll_config);

        mIv_slide_icon.setOnClickListener(this);
        mIv_qr.setOnClickListener(this);
        mIv_scan.setOnClickListener(this);
        config.setOnClickListener(this);
        //设置radiogroup
        rg_navi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_fame:
                        mVp_content.setCurrentItem(0, false);
                        break;
                    case R.id.rb_map:
                        mVp_content.setCurrentItem(1, false);
                        break;
                    case R.id.rb_relation:
                        mVp_content.setCurrentItem(2, false);
                        break;
                    case R.id.rb_circle:
                        mVp_content.setCurrentItem(3, false);
                        break;
                }
            }
        });

        mVp_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                
            }

            @Override
            public void onPageSelected(int position) {
                    
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        mDl_root = (DrawerLayout) findViewById(R.id.dl_root);
        mIv_icon = (AvatarImageView) findViewById(R.id.iv_icon);
        mIv_camera = (ImageView) findViewById(R.id.iv_camera);
        mIb_menu = (ImageButton) findViewById(R.id.ib_menu);
        mVp_content = (NoScrollViewPager) findViewById(R.id.vp_content);
        //设置vp是否禁止滑动
        mVp_content.setPagingEnabled(false);

        mIv_icon.setOnClickListener(this);
        mIv_camera.setOnClickListener(this);
        mIb_menu.setOnClickListener(this);

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        mVp_content.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_camera:

                break;
            case R.id.iv_scan:
                //跳转到扫一扫页面
                Intent intent_scan = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent_scan);
                break;
            case R.id.iv_slide_icon:
                //跳转到个人信息设置页面
                Intent intent_setting = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent_setting);
                break;
            case R.id.ib_menu:
                mDl_root.openDrawer(Gravity.LEFT);
                break;
            case R.id.iv_qr:
                //跳转到我的二维码
                Intent intent_qr = new Intent(MainActivity.this, QRCodeActivity.class);
                startActivity(intent_qr);
                break;
            case R.id.ll_config:
                Intent intent_config = new Intent(MainActivity.this, SysSettingActivity.class);
                startActivity(intent_config);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initUserIcon();
        uploadUserMsg();
        initUserMsg();
    }

    private void uploadUserMsg() {
        Boolean isUpload = SPUtils.getBoolean(this, "isUpload", false);
        if (isUpload) {
            UserInfoBean userInfoBean = new UserInfoBean();
            userInfoBean.setUsername(SPUtils.getString(this, "nickname", "娇禾生物"));
            userInfoBean.setSignature(SPUtils.getString(this, "sign", "有人的地方就有江湖"));
            Gson mGson = new Gson();
            String json = mGson.toJson(userInfoBean);
            //上传
            upLoadUserInfo(json);
        }


    }

    private void upLoadUserInfo(String json) {
        //String phone = SPUtils.getString(this, "phone", "");
        //String token = SPUtils.getString(this, "token", "");
        String body = "phone=" + phone + "&token=" + token + "&data=" + json;
        RequestUtils.postJsonRequest(ConstantValues.CHANGE_USER_INFO, body, UIUtils.getContext(),
                new VolleyInterface(UIUtils.getContext(),
                        VolleyInterface.mResponseListener,
                        VolleyInterface.mErrorListener) {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            String result = response.getString("result");
                            if (result.equals("RC100")) {
                                System.out.println("上传成功");
                                SPUtils.putBoolean(getApplicationContext(), "isUpload", false);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                    }
                });


    }


    public void getInterImage() {
        String body = "phone=" + phone
                + "&token=" + token;
        RequestUtils.postJsonRequest(ConstantValues.ICON_GETHEAD_URL, body, UIUtils.getContext(),
                new VolleyInterface(UIUtils.getContext(),
                        VolleyInterface.mResponseListener,
                        VolleyInterface.mErrorListener) {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            String result = response.getString("result");
                            System.out.println("----请求成功" + result);
                            if (result.equals("RC100")) {
                                //图片在服务器上的地址
                                System.out.println("----请求成功");
                                String imgurl = response.getString("imgurl");
                                System.out.println("----请求成功" + imgurl);
                                Glide.with(getApplicationContext()).load(imgurl).asBitmap().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        mIv_icon.setImageBitmap(resource);
                                        mIv_slide_icon.setImageBitmap(resource);
                                        saveMyBitmap(resource);
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                    }
                });
    }

    /**
     * @param bitmap 剪裁后获取bitmap保存为本地jpg
     */
    public void saveMyBitmap(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory(), "crop_icon.jpg");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取用户信息
    public void getInterUserInfo() {
        String body = "phone=" + phone + "&token=" + token;
        RequestUtils.postJsonRequest(ConstantValues.GET_USER_INFO, body, UIUtils.getContext(),
                new VolleyInterface(UIUtils.getContext(),
                        VolleyInterface.mResponseListener,
                        VolleyInterface.mErrorListener) {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            String result = response.getString("result");
                            if (result.equals("RC100")) {
                                String data = response.getString("data");
                                System.out.println("----" + data);
                                Gson gson = new Gson();
                                UserInfoBean userInfoBean = gson.fromJson(data, UserInfoBean.class);
                                mTv_sign.setText(userInfoBean.getSignature());
                                mTv_id.setText(userInfoBean.getUsername());
                                SPUtils.putString(getApplicationContext(), "nickname", userInfoBean.getUsername());
                                System.out.println("----" + userInfoBean.getUsername());
                                SPUtils.putBoolean(getApplicationContext(), "isUpload", false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                    }
                });


    }

    class MainPagerAdapter extends FragmentPagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.create(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }


}
