package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_CROP_IMAGE = 2;
    private static final int RESULT_CAMERA_IMAGE = 3;
    public static String photoDir = Environment.getExternalStorageDirectory() + "/";
    private final String path = Environment.getExternalStorageDirectory() + "/crop_icon.jpg";
    private Uri imageUriFromCamera;
    private RelativeLayout ll_icon,ll_nickname;
    private PopupWindow mPopupWindow,nicknamePopup;
    private AvatarImageView iv_icon;
    private TextView mTv_nickname;
    private EditText ed_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        UIUtils.initStateBar(SettingActivity.this);
        initView();
        initData();

    }



    private void initData() {
        Boolean isCache = SPUtils.getBoolean(this, "isCache", false);
        if (isCache) {
            Uri uriFromFilePath = UriUtils.getUriFromFilePath(path);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriFromFilePath);
                iv_icon.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       // if (isUpload){
            String nickname = SPUtils.getString(this, "nickname", "娇禾生物");
            mTv_nickname.setText(nickname);
       // }
    }

    private void initView() {
        ImageButton ib_back = (ImageButton) findViewById(R.id.ib_back);
        mTv_nickname = (TextView) findViewById(R.id.tv_nickname);

        ib_back.setOnClickListener(this);
        mTv_nickname.setOnClickListener(this);
        ll_icon = (RelativeLayout) findViewById(R.id.ll_icon);
        ll_icon.setOnClickListener(this);
        iv_icon = (AvatarImageView) findViewById(R.id.iv_icon);
        ll_nickname= (RelativeLayout) findViewById(R.id.ll_nickname);
        ll_nickname.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.ll_nickname:
                changeNickname();
                break;
            case R.id.ll_icon:
                chagenUserIcon();
                break;
            case R.id.button_back:
                mPopupWindow.dismiss();
                break;
            case R.id.button_photo:
                pickImageFromCamera();
                break;
            case R.id.button_album:
                pickImageFromAlbum();
                break;
            case R.id.button_determime:
                String nickname = ed_nickname.getText().toString().trim();
                if (TextUtils.isEmpty(nickname)){
                    Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    SPUtils.putString(this,"nickname",nickname);
                    SPUtils.putBoolean(this,"isUpload",true);
                    nicknamePopup.dismiss();
                    mTv_nickname.setText(nickname);
                }
                break;
        }
    }


    private void changeNickname() {
        View nicknameLayout = LayoutInflater.from(this).inflate(R.layout.layout_nickname_popupwindow, null);
        nicknamePopup = new PopupWindow(nicknameLayout,WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,true);
        ed_nickname= (EditText) nicknameLayout.findViewById(R.id.et_nickname);
        Button button_determine= (Button) nicknameLayout.findViewById(R.id.button_determime);
        button_determine.setOnClickListener(this);
        nicknamePopup.setFocusable(true);
        nicknamePopup.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        nicknamePopup.showAtLocation(ll_icon, Gravity.CENTER, 0, 0);

    }

    private void pickImageFromAlbum() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void pickImageFromCamera() {
        imageUriFromCamera = getImageUri();
        //拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFromCamera);
        startActivityForResult(intent, RESULT_CAMERA_IMAGE);
    }

    private void chagenUserIcon() {
        //初始化popupwindow
        View popupLayout = LayoutInflater.from(this).inflate(R.layout.layout_popupwindow, null);
        mPopupWindow = new PopupWindow(popupLayout, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        //初始化popupwindow里面的控件
        Button button_photo = (Button) popupLayout.findViewById(R.id.button_photo);
        Button button_album = (Button) popupLayout.findViewById(R.id.button_album);
        Button button_back = (Button) popupLayout.findViewById(R.id.button_back);
        button_album.setOnClickListener(this);
        button_photo.setOnClickListener(this);
        button_back.setOnClickListener(this);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.showAtLocation(ll_icon, Gravity.CENTER, 0, 0);
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
                if (data == null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUriFromCamera);
                        mPopupWindow.dismiss();
                        SPUtils.putBoolean(this, "isCache", true);
                        iv_icon.setImageBitmap(bitmap);
                        saveMyBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Bundle extras = data.getExtras();
                    Bitmap photo = extras.getParcelable("data");
                    iv_icon.setImageBitmap(photo);
                    mPopupWindow.dismiss();
                    SPUtils.putBoolean(this, "isCache", true);
                    saveMyBitmap(photo); // 保存裁剪后的图片到SD
                }
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
}
