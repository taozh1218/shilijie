package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.SPUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        UIUtils.initStateBar(QRCodeActivity.this);
        initView();
    }

    private Bitmap createQR() {
        //生成二维码的种子
        String phone = SPUtils.getString(this, "phone", "");
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix matrix = qrCodeWriter.encode(phone, BarcodeFormat.QR_CODE, UIUtils.dp2px(250), UIUtils.dp2px(250));
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            //二维矩阵转为一维像素数组,也就是一直横着排了
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = Color.WHITE;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            //通过像素数组生成bitmap
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initView() {
        ImageButton ib_back = (ImageButton) findViewById(R.id.ib_back);
        ImageView iv_my_qrcode = (ImageView) findViewById(R.id.iv_my_qrcode);
        ImageView iv_scan = (ImageView) findViewById(R.id.iv_scan);

        ib_back.setOnClickListener(this);
        iv_scan.setOnClickListener(this);
        //生成二维码bitmap并显示在界面
        Bitmap qr = createQR();
        iv_my_qrcode.setImageBitmap(qr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.iv_scan:
                //跳转到二维码扫描界面
                Intent startScan = new Intent(this, ScanActivity.class);
                startActivity(startScan);
        }
    }
}
