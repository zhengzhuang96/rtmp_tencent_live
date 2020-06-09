package com.example.rtmp_tencent_live;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

import static android.content.ContentValues.TAG;

public class RcmpTencentLive implements PlatformView, MethodChannel.MethodCallHandler {

    //    private final TextView myNativeView;
    private TXCloudVideoView mView;
    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private TXBeautyManager mBeautyManager;
    boolean Mirror = true;
    boolean onFlashLight = true;    /// 摄像头打开状态

    RcmpTencentLive(Context context, BinaryMessenger messenger, int id, Map<String, Object> params) {
        //        TextView myNativeView = new TextView(context);
        //        myNativeView.setText(params.get("text").toString());
        //        this.myNativeView = myNativeView;
        Log.i("MyActivity","校验IDIDIDID" + id);
        MethodChannel methodChannel = new MethodChannel(messenger, "tencentlive_" + id);
        methodChannel.setMethodCallHandler(this);

//        String licenceURL = "http://license.vod2.myqcloud.com/license/v1/3398497c80bc447ed493826c2b45f333/TXLiveSDK.licence"; // 获取到的 licence url
//        String licenceKey = "c095e4c1b94e61d5dda523bcb3b080c9"; // 获取到的 licence key
        String licenceURL = params.get("licenceURL").toString(); // 获取到的 licence url
        String licenceKey = params.get("licenceKey").toString(); // 获取到的 licence key
        TXLiveBase.getInstance().setLicence(context, licenceURL, licenceKey);

        mLivePushConfig = new TXLivePushConfig();
        mLivePusher = new TXLivePusher(context);

        // 一般情况下不需要修改 config 的默认配置
        mLivePusher.setConfig(mLivePushConfig);

        mView = (TXCloudVideoView) LayoutInflater.from(context).inflate(R.layout.pusher_tx_cloud_view, null);
        mLivePusher.startCameraPreview(mView);

//        String rtmpURL = "rtmp://push.rundle.cn/live/29?txSecret=a09d849fe9ca92f9692affcf263c9388&txTime=5EE0462E"; //此处填写您的 rtmp 推流地址
        String rtmpURL = params.get("rtmpURL").toString(); //此处填写您的 rtmp 推流地址
        int ret = mLivePusher.startPusher(rtmpURL.trim());
        Log.i(TAG, "startRTMPPush: license 校验结果" + ret);
        if (ret == -5) {
            Log.i(TAG, "startRTMPPush: license 校验失败");
        }
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        Log.i("MyActivity","校验MyClass.getView() - get item number");
        if ("setText".equals(methodCall.method)) {
            // String text = (String) methodCall.arguments;
            // myNativeView.setText(text);
            // result.success(null);
            Log.i("MyActivity","校验MyClass.getView() - get item number");

            setTurnOnFlashLight();
        }

        if("setSwitchCamera".equals(methodCall.method)) {
            setSwitchCamera();
        }

        if("setTurnOnFlashLight".equals(methodCall.method)) {
            setTurnOnFlashLight();
        }

        if("setBeautyStyle".equals(methodCall.method)) {
            int text = (int) methodCall.arguments;
            setBeautyStyle(text);
        }

        if("setMirror".equals(methodCall.method)) {
            setMirror();
        }
    }

    /// 切换前后摄像头
    protected void setSwitchCamera() {
        mLivePusher.switchCamera();
    }

    /// 设置镜像模式
    protected void setMirror() {
        if(Mirror) {
            mLivePusher.setMirror(false);
            Mirror = false;
        } else {
            mLivePusher.setMirror(true);
            Mirror = true;
        }
    }

    /// 开启后置摄像头旁边的闪光灯
    /// 前提是打开了后置摄像头
    protected void setTurnOnFlashLight() {
        if(onFlashLight) {
            mLivePusher.turnOnFlashLight(false);
            onFlashLight = false;
        } else {
            mLivePusher.turnOnFlashLight(true);
            onFlashLight = true;
        }
    }

    /// 设置美颜
    /// 美颜风格，0表示光滑，1表示自然，2表示朦胧。
    protected void setBeautyStyle(int num) {
        mBeautyManager.setBeautyStyle(num);
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void dispose() {
        mLivePusher.stopPusher();
        mLivePusher.stopCameraPreview(true); //如果已经启动了摄像头预览，请在结束推流时将其关闭。
    }
}
