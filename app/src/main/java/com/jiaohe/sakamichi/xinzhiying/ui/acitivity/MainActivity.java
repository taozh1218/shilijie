package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.ui.view.AvatarImageView;
import com.jiaohe.sakamichi.xinzhiying.util.RequestUtils;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;
import com.jiaohe.sakamichi.xinzhiying.util.VolleyInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout mDl_root;
    private AvatarImageView mIv_icon;
    private ImageView mIv_camera;
    private ViewPager mVp_content;
    private ImageButton mIb_menu;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_CROP_IMAGE = 2;
    private final String path = Environment.getExternalStorageDirectory() + "/crop_icon.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
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
            case R.id.iv_icon:
                break;
            case R.id.btn_update:
                choosePic();
                break;
            case R.id.ib_menu:
                mDl_root.openDrawer(Gravity.LEFT);
                break;
        }
    }

    private void choosePic() { //跳转到图片选择界面
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
