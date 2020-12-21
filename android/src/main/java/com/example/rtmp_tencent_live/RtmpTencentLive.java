package com.example.rtmp_tencent_live;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.example.rtmp_tencent_live.liveroom.IMLVBLiveRoomListener;
import com.example.rtmp_tencent_live.liveroom.MLVBLiveRoomImpl;
import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import java.util.Map;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;
import static android.content.ContentValues.TAG;

public class RtmpTencentLive implements PlatformView, MethodChannel.MethodCallHandler {

    //    private final TextView myNativeView;
    private TXCloudVideoView mView;
    private TXLivePusher mLivePusher;
    private TXLivePushConfig mLivePushConfig;
    private TXBeautyManager mBeautyManager;
    boolean Mirror = false;
    boolean onFlashLight = true;    /// 摄像头打开状态
    String rtmpURL; // 推荐直播
    private Context mContext;
    private MLVBLiveRoomImpl mLVBLiveRoomImpl;


    int _style = 0;             // 美颜算法：  0：光滑  1：自然  2：朦胧
    int _beautyLevel = 0;       // 磨皮等级： 取值为 0-9.取值为 0 时代表关闭美颜效果.默认值: 0,即关闭美颜效果.
    int _whiteningLevel = 0;    // 美白等级： 取值为 0-9.取值为 0 时代表关闭美白效果.默认值: 0,即关闭美白效果.
    int _ruddyLevel = 0;        // 红润等级： 取值为 0-9.取值为 0 时代表关闭美白效果.默认值: 0,即关闭美白效果.

    RtmpTencentLive(Context context, BinaryMessenger messenger, int id, Map<String, Object> params) {
        //        TextView myNativeView = new TextView(context);
        //        myNativeView.setText(params.get("text").toString());
        //        this.myNativeView = myNativeView;
        mContext = context;

        mView = (TXCloudVideoView) LayoutInflater.from(context).inflate(R.layout.pusher_tx_cloud_view, null);

        mLivePusher = new TXLivePusher(context);

        mLivePusher.startCameraPreview(mView);

        mLivePushConfig = new TXLivePushConfig();

        mLVBLiveRoomImpl = new MLVBLiveRoomImpl(mContext);

        // 一般情况下不需要修改 config 的默认配置
        mLivePusher.setConfig(mLivePushConfig);

        mLVBLiveRoomImpl.sharedInstance(mContext);

        Log.i("MyActivity","tencentlive_" + id);
        MethodChannel methodChannel = new MethodChannel(messenger, "rtmptencentlivepush_" + id);
        methodChannel.setMethodCallHandler(this);

        rtmpURL = params.get("rtmpURL").toString();
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        Log.i("MyActivity","校验MyClass.getView() - get item number");
        Map<String, Object> request = (Map<String, Object>) methodCall.arguments;

        switch (methodCall.method) {
            case "setLicence":
                setLicence(mContext, request, result);
                break;
            case "startLive":
                result.success(startLive());
                break;
            case "setSwitchCamera":
                setSwitchCamera();
                break;
            case "setTurnOnFlashLight":
                setTurnOnFlashLight();
                break;
            case "setMirror":
                setMirror();
                break;
            case "setDermabrasion":
                setDermabrasion(request, result);
                break;
            case "setWhitening":
                setWhitening(request, result);
                break;
            case "setUpRuddy":
                setUpRuddy(request, result);
                break;
            case "setOrientationChange":
                onOrientationChange(request, result);
            default:
                result.notImplemented();
        }
    }

    /// 初始化Licence，设置licenceURL，licenceKey
    protected void setLicence(Context context, Map<String, Object> request, MethodChannel.Result result) {
        Log.i("init", "初始化Licence，设置licenceURL，licenceKey");
        String licenceUrl = request.get("url").toString();
        String licenseKey = request.get("key").toString();
        TXLiveBase.getInstance().setLicence(context, licenceUrl, licenseKey);
        result.success("success");
    }

    /// 开始直播
    protected int startLive() {
        Log.i(TAG, "开始直播");
        int ret = mLivePusher.startPusher(rtmpURL.trim());
        Log.i(TAG, "startRTMPPush: license 校验结果" + ret);
        if (ret == -5) {
            Log.i(TAG, "startRTMPPush: license 校验失败");
        }
        return ret;
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

    /// 设置磨皮
    protected void setDermabrasion(Map<String, Object> request, MethodChannel.Result result) {
        _beautyLevel = (int) request.get("val");
        setBeautyFilter();
        result.success("success");
    }

    /// 设置美白
    protected void setWhitening(Map<String, Object> request, MethodChannel.Result result) {
        _whiteningLevel = (int) request.get("val");
        setBeautyFilter();
        result.success("success");
    }

    /// 设置红润
    protected void setUpRuddy(Map<String, Object> request, MethodChannel.Result result) {
        _ruddyLevel = (int) request.get("val");
        setBeautyFilter();
        result.success("success");
    }

    /// 设置美颜
    protected void setBeautyFilter() {
        mLivePusher.setBeautyFilter(_style, _beautyLevel, _whiteningLevel, _ruddyLevel);
    }

    protected void onOrientationChange(Map<String, Object> request, MethodChannel.Result result) {
        boolean isPortrait = (boolean) request.get("val");
        if (isPortrait) {
            mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_DOWN);
            mLivePusher.setConfig(mLivePushConfig);
            // mLivePusher.setRenderRotation(0);
        } else {
            mLivePushConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT);
            mLivePusher.setConfig(mLivePushConfig);
            // 因为采集旋转了，为了保证本地渲染是正的，则设置渲染角度为90度。
            // mLivePusher.setRenderRotation(90);
        }
    }

//    public abstract void requestJoinAnchor(String reason, IMLVBLiveRoomListener.RequestJoinAnchorCallback callback);


    /// 连麦申请
    protected void setLianmai() {
//        mLVBLiveRoomImpl.requestRoomPK("123123", (IMLVBLiveRoomListener.RequestRoomPKCallback) requestRoomPKCallbackClass);
    }


    @Override
    public View getView() {
        return mView;
    }


    @Override
    public void dispose() {
        mLivePusher.stopPusher();
        mLivePusher.stopCameraPreview(true); //如果已经启动了摄像头预览，请在结束推流时将其关闭。

        mLVBLiveRoomImpl.destroySharedInstance();
    }
}
