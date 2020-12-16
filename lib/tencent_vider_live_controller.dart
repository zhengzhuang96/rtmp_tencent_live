/*
 * @Author: zhengzhuang
 * @Date: 2020-08-15 11:43:27
 * @LastEditors: zhengzhuang
 * @LastEditTime: 2020-08-18 11:04:06
 * @Description: In User Settings Edit
 * @FilePath: /rtmp_tencent_live/lib/tencent_vider_live_controller.dart
 */
import 'package:flutter/services.dart';

class TencentViderLiveController {
  
  final MethodChannel _channel;
  
  TencentViderLiveController(int id) : _channel = MethodChannel('rtmptencentvideolive_$id') {
    _channel.setMethodCallHandler(_onMethodCall);
  }

  Future<bool> _onMethodCall(MethodCall call) async {
    throw MissingPluginException('${call.method} was invoked but has no handler');
  }

  /// 开始播放直播画面
  Future playStart(playUrl) async {
    return await _channel.invokeMethod('playStart', {"playUrl": playUrl});
  }

  /// 设置画面旋转方向
  /// 1: 正常播放（Home 键在画面正下方）
  /// 2: 画面顺时针旋转 270 度（Home 键在画面正左方）
  Future setRenderRotation(String direction) async {
    return await _channel.invokeMethod('setRenderRotation', {"direction": direction});
  }

  /// 设置播放渲染模式
  /// 1: 等比
  /// 2: 铺满
  Future setRenderMode(String renderMode) async {
    return await _channel.invokeMethod('setRenderMode', {"renderMode": renderMode});
  }

  /// 停止播放
  Future stopPlay() async {
    return await _channel.invokeMethod('stopPlay');
  }

  /// 暂停播放
  Future pause() async {
    return await _channel.invokeMethod('pause');
  }

  /// 恢复播放
  Future resume() async {
    return await _channel.invokeMethod('resume');
  }

  /// 视频截图
  Future screenCapture() async {
    return await _channel.invokeMethod('screenCapture');
  }

  /// 横屏推流
  Future setOrientationChange(bool isPortrait) async {
    return await _channel.invokeMethod('setOrientationChange', {"renderMode": isPortrait});
  }
}