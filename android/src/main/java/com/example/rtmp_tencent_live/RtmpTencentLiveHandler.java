package com.example.rtmp_tencent_live;
import android.content.Context;
import android.util.Log;
import com.tencent.rtmp.TXLiveBase;
import java.util.Map;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class RtmpTencentLiveHandler implements MethodChannel.MethodCallHandler {

    Context context;

    RtmpTencentLiveHandler(Context context){
        this.context = context;
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        Map<String, Object> request = (Map<String, Object>) methodCall.arguments;
        switch (methodCall.method) {
            case "setLicence":
                setLicence(request, result);
                break;
            default:
                result.notImplemented();
        }
    }

    private void setLicence(Map<String, Object> request, MethodChannel.Result result){
        Log.i("testArr", "获取数据");
        String licenceUrl = request.get("url").toString();
        String licenseKey = request.get("key").toString();
        TXLiveBase.getInstance().setLicence(context, licenceUrl, licenseKey);
        result.success("success");
    }

}