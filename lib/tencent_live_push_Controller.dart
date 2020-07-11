import 'package:flutter/services.dart';

typedef void PushEventListener(Map args);

class TencentLivePushController {

  TencentLivePushController(int id) : _channel = MethodChannel('rtmptencentlivepush_$id') {
    _channel.setMethodCallHandler(_onMethodCall);
  }


  final MethodChannel _channel;

  Future<bool> _onMethodCall(MethodCall call) async {
    throw MissingPluginException('${call.method} was invoked but has no handler');
  }

  /// 开始直播
  Future startLive()async {
    return await _channel.invokeMethod('startLive');
  }

  /// 切换前后摄像头
  Future setSwitchCamera()async {
    return await _channel.invokeMethod<int>('setSwitchCamera');
  }

  /// 打开后置灯光
  Future setTurnOnFlashLight()async {
    return await _channel.invokeMethod('setTurnOnFlashLight');
  }

  /// 镜像模式
  Future setMirror()async {
    return await _channel.invokeMethod('setMirror');
  }

  /// 磨皮
  Future setDermabrasion(v) async {
    return await _channel.invokeMethod("setDermabrasion", {"val": v.toInt()});
  }

  /// 美白
  Future setWhitening(v) async {
    return await _channel.invokeMethod('setWhitening', { 'val': v.toInt() });
  }

  /// 红润
  Future setUpRuddy(v) async {
    return await _channel.invokeMethod('setUpRuddy', { 'val': v.toInt() });
  }
}