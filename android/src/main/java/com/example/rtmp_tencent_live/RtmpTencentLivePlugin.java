package com.example.rtmp_tencent_live;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformViewRegistry;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;

/** RtmpTencentLivePlugin */
public class RtmpTencentLivePlugin implements FlutterPlugin {

  private BinaryMessenger messenger;
  private PlatformViewRegistry platformViewRegistry;
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@Nullable FlutterPluginBinding flutterPluginBinding) {
    assert flutterPluginBinding != null;
    this.messenger = flutterPluginBinding.getBinaryMessenger();
    this.platformViewRegistry = flutterPluginBinding.getPlatformViewRegistry();
    new MethodChannel(this.messenger, "rtmp_tencent_live_flutter").setMethodCallHandler(new RtmpTencentLiveHandler(flutterPluginBinding.getApplicationContext()));

    platformViewRegistry.registerViewFactory("TencentLive", new RtmpTencentLiveFactory(messenger));
    platformViewRegistry.registerViewFactory("TencentVideoLive", new VideoLiveFactory(messenger));
  }
//
//  @Override
//  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
//    Activity activity = activityPluginBinding.getActivity();
//    platformViewRegistry.registerViewFactory("TencentVideoLive", new VideoLiveFactory(messenger));
//  }

//  @Override
//  public void onDetachedFromActivityForConfigChanges() {
//
//  }
//
//  @Override
//  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
//
//  }
//
//  @Override
//  public void onDetachedFromActivity() {
//
//  }

  @Override
  public void onDetachedFromEngine(@Nullable FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
