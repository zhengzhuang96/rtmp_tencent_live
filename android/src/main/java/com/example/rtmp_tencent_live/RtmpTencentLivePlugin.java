package com.example.rtmp_tencent_live;
import android.app.Activity;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformViewRegistry;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;

/** RtmpTencentLivePlugin */
public class RtmpTencentLivePlugin implements FlutterPlugin, ActivityAware {


  private BinaryMessenger messenger;
  private PlatformViewRegistry platformViewRegistry;
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.messenger = flutterPluginBinding.getBinaryMessenger();
    this.platformViewRegistry = flutterPluginBinding.getPlatformViewRegistry();
    new MethodChannel(this.messenger, "rtmp_tencent_live_flutter").setMethodCallHandler(new RtmpTencentLiveHandler(flutterPluginBinding.getApplicationContext()));
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    Activity activity = activityPluginBinding.getActivity();
    platformViewRegistry.registerViewFactory("TencentLive", new RtmpTencentLiveFactory(messenger, activity));
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
