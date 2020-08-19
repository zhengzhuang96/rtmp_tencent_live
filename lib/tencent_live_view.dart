/*
 * @Author: zhengzhuang
 * @Date: 2020-08-15 11:35:16
 * @LastEditors: zhengzhuang
 * @LastEditTime: 2020-08-17 14:46:05
 * @Description: In User Settings Edit
 * @FilePath: /rtmp_tencent_live/lib/tencent_live_view.dart
 */
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:rtmp_tencent_live/tencent_live_push_Controller.dart';

typedef Future<void> _OnLiveCreated(TencentLivePushController controller);

class TencentLiveView extends StatefulWidget {
  const TencentLiveView({Key key, this.rtmpURL, this.onCreated})
      : super(key: key);

  final String rtmpURL;
  final _OnLiveCreated onCreated;

  @override
  _TencentLiveViewState createState() => _TencentLiveViewState();
}

class _TencentLiveViewState extends State<TencentLiveView> {
  // MethodChannel _channel;

  Future<void> onMyViewCreated(int id) async {
    print('=======abdsasda====>>>>');
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

  // Future<void> setText(String text) async {
  //   assert(text != null);
  //   return _channel.invokeMethod('setText', text);
  // }

  Widget getMyPatformView() {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: "TencentLive",
        creationParams: <String, dynamic>{"rtmpURL": widget.rtmpURL},
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: onMyViewCreated,
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      return UiKitView(
        viewType: 'TencentLive',
        creationParams: <String, dynamic>{"rtmpURL": widget.rtmpURL},
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
