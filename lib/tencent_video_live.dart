/*
 * @Author: zhengzhuang
 * @Date: 2020-08-15 11:31:37
 * @LastEditors: zhengzhuang
 * @LastEditTime: 2020-12-20 12:48:00
 * @Description: In User Settings Edit
 * @FilePath: /rtmp_tencent_live/lib/tencent_video_live.dart
 */
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:rtmp_tencent_live/tencent_vider_live_controller.dart';

typedef Future<void> _OnLiveCreated(TencentViderLiveController controller);

class TencentVideoLive extends StatefulWidget {
  const TencentVideoLive({Key key, this.flvUrl, this.onCreated})
      : super(key: key);

  final String flvUrl;
  final _OnLiveCreated onCreated;

  @override
  _TencentVideoLiveState createState() => _TencentVideoLiveState();
}

class _TencentVideoLiveState extends State<TencentVideoLive> {

  Widget getMyPatformView() {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: "TencentVideoLive",
        creationParams: <String, dynamic>{"flvUrl": widget.flvUrl},
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: onMyViewCreated,
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      return UiKitView(
        viewType: 'TencentVideoLive',
        creationParams: <String, dynamic>{"flvUrl": widget.flvUrl},
        creationParamsCodec: const StandardMessageCodec(),
        onPlatformViewCreated: onMyViewCreated,
      );
    }

    return Text('$defaultTargetPlatform is not yet supported by this plugin');
  }

  Future<void> onMyViewCreated(int id) async {
    final TencentViderLiveController controller = TencentViderLiveController(
      id,
    );
    if (widget.onCreated != null) {
      widget.onCreated(controller);
    }
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
