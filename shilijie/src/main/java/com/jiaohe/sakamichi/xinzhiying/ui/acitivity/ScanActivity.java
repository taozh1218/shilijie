package com.jiaohe.sakamichi.xinzhiying.ui.acitivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.client.android.AutoScannerView;
import com.google.zxing.client.android.BaseCaptureActivity;
import com.jiaohe.sakamichi.xinzhiying.R;
import com.jiaohe.sakamichi.xinzhiying.util.LogUtils;
import com.jiaohe.sakamichi.xinzhiying.util.UIUtils;

public class ScanActivity extends BaseCaptureActivity implements View.OnClickListener {

    private AutoScannerView mAsv_custom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        UIUtils.initStateBar(ScanActivity.this);
        initView();
    }

    @Override
    public SurfaceView getSurfaceView() {
        return (SurfaceView) findViewById(R.id.sv_root);
    }

    @Override
    public void dealDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        LogUtils.d("dealDecode ~~~~~ " + rawResult.getText() + " " + barcode + " " + scaleFactor);
        playBeepSoundAndVibrate(true, true);
        Toast.makeText(this, rawResult.getText(), Toast.LENGTH_LONG).show();
    }
    

    private void initView() {
        ImageButton ib_back = (ImageButton) findViewById(R.id.ib_back);
        mAsv_custom = (AutoScannerView) findViewById(R.id.asv_custom);
        ib_back.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAsv_custom.setCameraManager(cameraManager);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ib_back:
                finish();
                break;
         }
    }


}
