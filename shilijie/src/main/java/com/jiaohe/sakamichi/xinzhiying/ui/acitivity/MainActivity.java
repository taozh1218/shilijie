package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
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
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.bean.UserInfoBean;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.global.MyApplication;
import com.jiaohe.sakamichi.xinzhiying.ui.fragment.FragmentFactory;
import com.jiaohe.sakamichi.xinzhiying.ui.fragment.MapFragment;
import com.jiaohe.sakamichi.xinzhiying.ui.fragment.RelationFragment;
import com.jiaohe.sakamichi.xinzhiying.ui.view.AvatarImageView;
import com.jiaohe.sakamichi.xinzhiying.ui.view.NoScrollViewPager;
import com.jiaohe.sakamichi.xinzhiying.util.AppManager;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

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
    private Handler handler = new Handler();
    private RadioGroup mRg_navi;
    //根据被选择的fragment来设置顶部菜单键
    private int CHOOSE_FRAGMENT =1;
    private LinearLayout ll_toolbar;
    private ImageView iv_circlePen,iv_circlePhoto,iv_circleVideo;
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UIUtils.initStateBar(MainActivity.this); //设置透明状态栏
        AppManager.getAppManager().addActivity(this);
        initView(); //初始化MainActivity中控件
        initSlideMenuView(); //初始化侧边栏中控件
        initData();
        initUserIcon();
        initUserMsg();
        initPhone();
    }

    private void initPhone() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < phone.length(); i++) {
            char c = phone.charAt(i);
            if (i >= 3 && i <= 6) {
                sb.append('*');
            } else {
                sb.append(c);
            }
        }
        mTv_phone.setText("电话号码："+sb.toString());



    }
    private void initData() {
        phone = SPUtils.getString(this, "phone", "");
        token = SPUtils.getString(this, "token", "");
        System.out.println("----"+token);
    }

    private void initUserMsg() {
        Boolean isUpload = SPUtils.getBoolean(this, "isUpload", false);
        if (isUpload) {
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
        LinearLayout ll_address = (LinearLayout) findViewById(R.id.ll_address);
        LinearLayout ll_authentication = (LinearLayout) findViewById(R.id.ll_authentication);
        LinearLayout ll_favourite = (LinearLayout) findViewById(R.id.ll_favourite);
        LinearLayout ll_market = (LinearLayout) findViewById(R.id.ll_market);
        LinearLayout ll_order = (LinearLayout) findViewById(R.id.ll_order);
        LinearLayout ll_wallet = (LinearLayout) findViewById(R.id.ll_wallet);
        LinearLayout ll_config = (LinearLayout) findViewById(R.id.ll_config);
        LinearLayout ll_sign = (LinearLayout) findViewById(R.id.ll_sign);


        mIv_slide_icon.setOnClickListener(this);
        mIv_qr.setOnClickListener(this);
        mIv_scan.setOnClickListener(this);
        ll_config.setOnClickListener(this);
        ll_sign.setOnClickListener(this);

    }

    private void initView() {
        mDl_root = (DrawerLayout) findViewById(R.id.dl_root);
        mIv_icon = (AvatarImageView) findViewById(R.id.iv_icon);
        mIv_camera = (ImageView) findViewById(R.id.iv_camera);
        mIb_menu = (ImageButton) findViewById(R.id.ib_menu);
        mVp_content = (NoScrollViewPager) findViewById(R.id.vp_content);
        mRg_navi = (RadioGroup) findViewById(R.id.rg_navi);
        ll_toolbar= (LinearLayout) findViewById(R.id.ll_toolbar);
        mIv_icon.setOnClickListener(this);
        mIv_camera.setOnClickListener(this);
        mIb_menu.setOnClickListener(this);
        //绑定radiogroup和viewpager
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        mVp_content.setAdapter(adapter);
        mVp_content.setPagingEnabled(false);
        mVp_content.setOffscreenPageLimit(2);
        mRg_navi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_fame:
                        CHOOSE_FRAGMENT=1;
                        mIv_camera.setImageResource(R.drawable.photo_icon);
                        mVp_content.setCurrentItem(0, false);
                        break;
                    case R.id.rb_map:
                        CHOOSE_FRAGMENT=2;
                        mIv_camera.setImageResource(R.drawable.slide_menu_search);
                        mVp_content.setCurrentItem(1, false);
                        break;
                    case R.id.rb_relation:
                        CHOOSE_FRAGMENT=3;
                        mIv_camera.setImageResource(R.drawable.btu_add);
                        mVp_content.setCurrentItem(2, false);
                        break;
                    case R.id.rb_circle:
                        CHOOSE_FRAGMENT=4;
                        mIv_camera.setImageResource(R.drawable.btu_add);
                        mVp_content.setCurrentItem(3, false);
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_camera:
                try {
                    EMClient.getInstance().contactManager().addContact("18396804155","666666");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                switch (CHOOSE_FRAGMENT){
                    case 1:


                        break;
                    case 2:


                        break;
                    case 3:


                        break;
                    case 4:
                        showCircleMenu();
                        break;
                }

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
                //跳转到设置页面
                Intent intent_config = new Intent(MainActivity.this, SysSettingActivity.class);
                startActivity(intent_config);
                break;
            case R.id.ll_sign:
                //跳转到签名页面
                Intent intent_sign = new Intent(MainActivity.this, ChangeSignatureActivity.class);
                startActivity(intent_sign);
                break;
            case R.id.iv_circlePen:
                Intent intent = new Intent(MainActivity.this,CircleIssueActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showCircleMenu() {
        View circleMenu = LayoutInflater.from(this).inflate(R.layout.layout_cricle_menu, null);
        PopupWindow popupWindow  =new PopupWindow(circleMenu, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        popupWindow.showAsDropDown(ll_toolbar,width*100/193,14);
        iv_circlePen= (ImageView) circleMenu.findViewById(R.id.iv_circlePen);
        iv_circlePen.setOnClickListener(this);
        iv_circlePhoto= (ImageView) circleMenu.findViewById(R.id.iv_circlePhoto);
        iv_circlePhoto.setOnClickListener(this);
        iv_circleVideo= (ImageView) circleMenu.findViewById(R.id.iv_circleVideo);
        iv_circleVideo.setOnClickListener(this);

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
        RequestUtils.postJsonRequest(ConstantValues.GET_STS_URL, body, UIUtils.getContext(),
                new VolleyInterface(UIUtils.getContext(),
                        VolleyInterface.mResponseListener,
                        VolleyInterface.mErrorListener) {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            String id = response.getString("accessKeyId");
                            String secret = response.getString("accessKeySecret");
                            String securityToken = response.getString("securityToken");
                            //获取STS成功 异步请求下载头像头像
                            //RequestUtils.downloadIcon(id, secret, securityToken);
                            downIcon(id, secret, securityToken);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                    }
                });
    }

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
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Uri uriFromFilePath = UriUtils.getUriFromFilePath(path);
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), uriFromFilePath);
                                mIv_icon.setImageBitmap(bitmap);
                                mIv_slide_icon.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

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
                                Gson gson = new Gson();
                                UserInfoBean userInfoBean = gson.fromJson(data, UserInfoBean.class);
                                mTv_sign.setText(userInfoBean.getSignature());
                                mTv_id.setText(userInfoBean.getUsername());
                                SPUtils.putString(getApplicationContext(), "nickname", userInfoBean.getUsername());
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
            if (position == 2) {
                return new RelationFragment();
            }
            if (position == 1) {
                return MapFragment.newInstance();
            }
            return FragmentFactory.create(position);
        }

        @Override
        public int getCount() {
            return 4;
        }


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(); // 调用双击退出函数
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {// isExit == false的简化版
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }


}
