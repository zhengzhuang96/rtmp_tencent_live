/*
 * @Author: zhengzhuang
 * @Date: 2020-06-09 11:46:10
 * @LastEditors: zhengzhuang
 * @LastEditTime: 2020-08-15 11:38:48
 * @Description: In User Settings Edit
 * @FilePath: /rtmp_tencent_live/lib/tencent_live.dart
 */
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/foundation.dart';

class TencentLive {
  static TencentLive _instance;

  static TencentLive get instance {
    if (_instance == null) {
      _instance = TencentLive();

    }
    return _instance;
  }

  TencentLive(){
    _channel.setMethodCallHandler(_handleMethod);
  }

  final MethodChannel _channel = const MethodChannel('rtmp_tencent_live_flutter');

  Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {

    }
  }

  /// 初始化SDK
  ///
  /// license申请请参考 https://console.cloud.tencent.com/live/license
  Future<void> init({
    @required String licenseUrl,
    @required String licenseKey,
  }) async {
    // licenseUrl是以http开头的, 在ios上会报鉴权失败(-5), 把 http 改成 https , 才能正常推流!
    final httpsUrl = Uri.parse(licenseUrl).scheme == 'http'
        ? licenseUrl.replaceFirst('http', 'https')
        : Uri.parse(licenseUrl).scheme;
    return await _channel.invokeMethod("setLicence", {"url": httpsUrl, "key": licenseKey});
  }
}
