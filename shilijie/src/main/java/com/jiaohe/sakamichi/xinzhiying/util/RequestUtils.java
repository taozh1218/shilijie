package com.jiaohe.sakamichi.xinzhiying.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jiaohe.sakamichi.xinzhiying.global.ConstantValues;
import com.jiaohe.sakamichi.xinzhiying.global.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by sakamichi on 16/9/21.
 */

public class RequestUtils {
    private static RequestQueue mRequestQueue;
    private RequestUtils() {
        super();
    }


    public static synchronized RequestQueue getInstance(Context ctx) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(ctx);
        }
        return mRequestQueue;
    }

    public static void postJsonRequest(String url, String body, Context ctx, VolleyInterface vi) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, vi.loadingListener(), vi.errorListener()) {
            @Override
            public String getBodyContentType() {
                if (getMethod() == Method.POST) {
                    return "application/x-www-form-urlencoded";
                }
                return super.getBodyContentType();
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance(ctx).add(request);
    }


    /**
     * @param id
     * @param secret
     * @param token
     * @param path   上传头像到阿里云oss服务器
     */
    public static void updateIcon(String id, String secret, String token, String path) {
        String endpoint = "http://oss.xinzhiying.net";
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(id, secret, token);
        OSS oss = new OSSClient(UIUtils.getContext(), endpoint, credentialProvider);
        // 构造上传请求
        final PutObjectRequest put = new PutObjectRequest("jiaohe", "images/app/headimg/" + SPUtils.getString(UIUtils.getContext(), "phone", "") + "_icon", path);
        put.setCallbackParam(new HashMap<String, String>() {
            {
                put("callbackUrl", ConstantValues.ICON_CALLBACK_URL);
                put("callbackHost", "www.xinzhiying.net");
                put("callbackBodyType", "application/json");
                //拼装回调请求体
                String body = "{\"phone\":" + "\"" + SPUtils.getString(UIUtils.getContext(), "phone", "") + "\""
                        + ",\"token\":" + "\"" + SPUtils.getString(UIUtils.getContext(), "token", "") + "\""
                        + ",\"object\":" + "\"" + SPUtils.getString(UIUtils.getContext(), "phone", "") + "_icon" + "\"" + "}";
                put("callbackBody", body);
            }
        });
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                // 只有设置了servercallback，这个值才有数据
                String serverCallbackReturnJson = result.getServerCallbackReturnBody();
                Log.d("servercallback", serverCallbackReturnJson);
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        // task.cancel(); // 可以取消任务
        task.waitUntilFinished(); // 可以等待任务完成
    }

    public static void downloadIcon(String id, String secret, final String token) {
        String endpoint = "http://oss.xinzhiying.net";
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(id, secret, token);
        final OSS oss = new OSSClient(UIUtils.getContext(), endpoint, credentialProvider);
        GetObjectRequest get = new GetObjectRequest("jiaohe", "images/app/headimg/" + SPUtils.getString(UIUtils.getContext(), "phone", "")+"_icon");
        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                try {
                    OutputStream os = new FileOutputStream(new File(Environment.getExternalStorageDirectory(),"crop_icon.jpg"));

                byte[] buffer = new byte[2048];
                int len;
                StringBuilder stringBuilder = new StringBuilder();
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        os.write(buffer,0,len);
                    }
                 os.close();
                    inputStream.close();
                    SPUtils.putBoolean(MyApplication.getContext(), "isCache",true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished(); // 可以等待任务完成

    }
}
