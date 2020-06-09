import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/foundation.dart';

typedef Future<void> _OnLiveCreated(MethodChannel controller);

class TencentLive extends StatefulWidget {
  const TencentLive(
      {Key key,
        this.licenceURL,
        this.licenceKey,
        this.rtmpURL,
        @required this.onCreated})
      :super(key: key);

  final String licenceURL;
  final String licenceKey;
  final String rtmpURL;
  final _OnLiveCreated onCreated;

  @override
  _TencentLiveState createState() => _TencentLiveState();
}

class _TencentLiveState extends State<TencentLive> {
  MethodChannel _channel;

  onMyViewCreated(int id) async {
    _channel = new MethodChannel('tencentlive_$id');
    setText("哈哈哈adf");
    return widget.onCreated(_channel);
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
          "licenceURL": widget.licenceURL,
          "licenceKey": widget.licenceKey,
          "rtmpURL": widget.rtmpURL
        },
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: onMyViewCreated,
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      return UiKitView(
        viewType: 'TencentLive',
        creationParams: <String, dynamic>{
          "licenceURL": widget.licenceURL,
          "licenceKey": widget.licenceKey,
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
