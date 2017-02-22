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
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.ui.view.AvatarImageView;
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
    private ViewPager mVp_content;
    private ImageButton mIb_menu;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_CROP_IMAGE = 2;
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

        UIUtils.initStateBar(MainActivity.this); //设置透明状态栏
        initView(); //初始化MainActivity中控件
        initSlideMenuView(); //初始化侧边栏中控件
        initUserIcon();
        initUserMsg();
    }

    private void initUserMsg() {





    }

    private void initUserIcon() {
        Boolean isCache = SPUtils.getBoolean(this, "isCache", false);

        if (isCache){
            Glide.with(this).load(path).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIv_slide_icon);
            Uri uriFromFilePath = UriUtils.getUriFromFilePath(path);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriFromFilePath);
                mIv_icon.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
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
        mTv_weather = (TextView) findViewById(R.id.tv_weather);
        mTv_temperature = (TextView) findViewById(R.id.tv_temperature);
        mTv_wind = (TextView) findViewById(R.id.tv_wind);
        mTv_location = (TextView) findViewById(R.id.tv_location);
        LinearLayout ll_address = (LinearLayout) findViewById(R.id.ll_address);
        LinearLayout authentication = (LinearLayout) findViewById(R.id.ll_authentication);
        LinearLayout join = (LinearLayout) findViewById(R.id.ll_join);
        LinearLayout favourite = (LinearLayout) findViewById(R.id.ll_favourite);
        LinearLayout market = (LinearLayout) findViewById(R.id.ll_market);
        LinearLayout order = (LinearLayout) findViewById(R.id.ll_order);
        LinearLayout wallet = (LinearLayout) findViewById(R.id.ll_wallet);
        LinearLayout config = (LinearLayout) findViewById(R.id.ll_config);

        mIv_slide_icon.setOnClickListener(this);
        mIv_qr.setOnClickListener(this);
        mIv_scan.setOnClickListener(this);
        config.setOnClickListener(this);
    }

    private void initView() {
        mDl_root = (DrawerLayout) findViewById(R.id.dl_root);
        mIv_icon = (AvatarImageView) findViewById(R.id.iv_icon);
        mIv_camera = (ImageView) findViewById(R.id.iv_camera);
        mIb_menu = (ImageButton) findViewById(R.id.ib_menu);
        mVp_content = (ViewPager) findViewById(R.id.vp_content);

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
    }

    /**
     * 打开
     */
    private void choosePic() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    /**
     * 获取STS认证
     */
    private void requestSTS() {
        String body = "phone=" + SPUtils.getString(UIUtils.getContext(), "phone", "")
                + "&token=" + SPUtils.getString(UIUtils.getContext(), "token", "");
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
                            //获取STS成功 请求上传头像
                            RequestUtils.updateIcon(id, secret, securityToken, path);
                            //straightUpload(id, secret, securityToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                    }
                });
    }

    public void getInterImage() {
        String body = "phone=" + SPUtils.getString(UIUtils.getContext(), "phone", "")
                + "&token=" + SPUtils.getString(UIUtils.getContext(), "token", "");
        RequestUtils.postJsonRequest(ConstantValues.ICON_GETHEAD_URL, body, UIUtils.getContext(),
                new VolleyInterface(UIUtils.getContext(),
                        VolleyInterface.mResponseListener,
                        VolleyInterface.mErrorListener) {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            String result = response.getString("result");
                            System.out.println("----请求成功"+result);
                            if (result.equals("RC100")){
                                //图片在服务器上的地址
                                System.out.println("----请求成功");
                                String imgurl = response.getString("imgurl");
                                System.out.println("----请求成功"+imgurl);
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

    class MainPagerAdapter extends FragmentPagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case RESULT_LOAD_IMAGE:
                    if (data.getData() != null) {
                        Uri iconUri = data.getData();
                        startCropImage(iconUri); //剪裁开始
                    }
                    break;
                case RESULT_CROP_IMAGE:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        saveMyBitmap(photo); // 保存裁剪后的图片到SD  
                    }
                    //图片剪裁并保存完毕，准备上传
                    requestSTS();
                    break;
            }
        }
    }

    /**
     * @param uri 需要剪裁照片的uri
     *            开启系统的剪裁界面
     */
    public void startCropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 图片处于可裁剪状态
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 是否之处缩放
        intent.putExtra("scale", true);
        // 设置图片的输出大小, 对于普通的头像,应该设置一下,可提高头像的上传速度
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        // 设置图片输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", true);
        // 关闭人脸识别
        intent.putExtra("noFaceDetection", false);
        startActivityForResult(intent, RESULT_CROP_IMAGE);
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

}
