package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.VolleyError;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.global.MyApplication;
import com.jiaohe.sakamichi.xinzhiying.util.CircleActivtyFactory;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class CircleBackgroundImageActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_CROP_IMAGE = 2;
    private static final int RESULT_CAMERA_IMAGE = 3;
    private Button mBt_photographic,mBt_photoAlbum,mBt_defaultImage;
    private ImageButton mIb_backCircle;
    public static String photoDir = Environment.getExternalStorageDirectory() + "/";
    private String path= MyApplication.getContext().getExternalCacheDir()+"/circle_bg.jpg";
    private Uri imageUriFromCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cricle_background_image);
        CircleActivtyFactory.getAppManager().addActivity(this);
        UIUtils.initStateBar(this);
        initView();
    }

    private void initView() {
        mIb_backCircle= (ImageButton) findViewById(R.id.ib_backCircle);
        mBt_defaultImage= (Button) findViewById(R.id.button_defaultImage);
        mBt_defaultImage.setOnClickListener(this);
        mBt_photographic= (Button) findViewById(R.id.button_photographic);
        mBt_photographic.setOnClickListener(this);
        mBt_photoAlbum= (Button) findViewById(R.id.button_photoAlbum);
        mBt_photoAlbum.setOnClickListener(this);
        mIb_backCircle= (ImageButton) findViewById(R.id.ib_backCircle1);
        mIb_backCircle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_defaultImage:
                Intent intent = new Intent(CircleBackgroundImageActivity.this,DefaultImageActivity.class);
                startActivity(intent);
                break;
            case R.id.ib_backCircle1:

            break;
            case R.id.button_photoAlbum:
                    openPhotoAlbum();
            break;
            case R.id.button_photographic:
                    openPhotographic();
            break;


        }
    }

    private void openPhotographic() {

        imageUriFromCamera = getImageUri();
        //拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFromCamera);
        startActivityForResult(intent, RESULT_CAMERA_IMAGE);
    }

    private void openPhotoAlbum() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case RESULT_LOAD_IMAGE:
                    if (resultCode == RESULT_CANCELED) {   //取消操作
                        return;
                    }
                    Uri iconUri = data.getData();
                    startCropImage(iconUri); //剪裁开始
                    break;
                case RESULT_CAMERA_IMAGE:
                    if (resultCode == RESULT_CANCELED) {
                        return;
                    }
                    startCropImage(imageUriFromCamera);
                    break;
                case RESULT_CROP_IMAGE:
                    if (resultCode == RESULT_CANCELED) {
                        return;
                    }
                    Bundle extras = data.getExtras();
                    Bitmap photo = extras.getParcelable("data");
                    SPUtils.putBoolean(this, "isCache", true);
                    saveMyBitmap(photo); // 保存裁剪后的图片到SD
                    SPUtils.putString(this,"imageId","4");
                    //图片剪裁并保存完毕，准备上传
                    requestSTS();
                    break;
             }
       }



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
                            //获取STS成功 请求上传背景
                            RequestUtils.updateBackgroupImage(id,secret,securityToken,path);
                            finish();
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
        File file = new File(getExternalCacheDir(), "circle_bg.jpg");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            System.out.println(file.getName());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据指定目录产生一条图片Uri
     */
    private static Uri getImageUri() {
        String imageName = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".jpg";
        String path = photoDir + imageName;
        return UriUtils.getUriFromFilePath(path);
    }

    private void startCropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 图片处于可裁剪状态
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 2);
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




}
