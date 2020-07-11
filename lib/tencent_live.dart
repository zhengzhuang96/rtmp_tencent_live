import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/foundation.dart';
import 'package:rtmp_tencent_live/tencent_live_push_Controller.dart';

typedef Future<void> _OnLiveCreated(TencentLivePushController controller);

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

class TencentLiveView extends StatefulWidget {
  const TencentLiveView(
      {Key key,
        this.rtmpURL,
        this.onCreated})
      :super(key: key);

  final String rtmpURL;
  final _OnLiveCreated onCreated;

  @override
  _TencentLiveViewState createState() => _TencentLiveViewState();
}

class _TencentLiveViewState extends State<TencentLiveView> {
  MethodChannel _channel;

  Future<void> onMyViewCreated(int id) async {
    final TencentLivePushController controller = TencentLivePushController(
      id,
    );
    if (widget.onCreated != null) {
      widget.onCreated(controller);
    }
//    _channel = new MethodChannel('tencentlive_$id');
//    setText("哈哈哈adf");
//    return widget.onCreated(_channel);
    /// return _channel;
  }

  Future<void> setText(String text) async {
    assert(text != null);
    return _channel.invokeMethod('setText', text);
  }

  Widget getMyPatformView() {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: "TencentLive",
        creationParams: <String, dynamic>{
          "rtmpURL": widget.rtmpURL
        },
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: onMyViewCreated,
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      return UiKitView(
        viewType: 'TencentLive',
        creationParams: <String, dynamic>{
          "rtmpURL": widget.rtmpURL
        },
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: onMyViewCreated,
      );
    }

    return Text('$defaultTargetPlatform is not yet supported by this plugin');
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Stack(
        children: <Widget>[
          getMyPatformView(),
        ],
      ),
    );
  }
}
