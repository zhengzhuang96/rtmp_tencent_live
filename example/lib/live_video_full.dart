/*
 * @Author: zhengzhuang
 * @Date: 2020-08-19 11:35:24
 * @LastEditors: zhengzhuang
 * @LastEditTime: 2020-08-19 11:55:38
 * @Description: In User Settings Edit
 * @FilePath: /rtmp_tencent_live/example/lib/live_video_full.dart
 */
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';
import 'package:rtmp_tencent_live/tencent_vider_live_controller.dart';

class LiveVideoFull extends StatefulWidget {
  @override
  _LiveVideoFullState createState() => _LiveVideoFullState();
}

class _LiveVideoFullState extends State<LiveVideoFull> {
  TencentViderLiveController _controller;

  @override
void initState() {
  super.initState();
  SystemChrome.setPreferredOrientations([
    DeviceOrientation.landscapeLeft,
    DeviceOrientation.landscapeLeft,
  ]);
}

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: WillPopScope(child: Scaffold(
        body: Stack(
          children: <Widget>[
            Center(
              child: Hero(
                tag: "player",
                child: TencentVideoLive(
                  onCreated: (controller) async {
                    _controller = controller;
                    _controller.playStart('http://play.rundle.cn/live/1386756491641393921.flv');
                    _controller.setRenderRotation('1');
                  },
                ),
              )
            ),
            Padding(
              padding: EdgeInsets.only(top: 25, right: 20),
              child: IconButton(
                icon: const BackButtonIcon(),
                color: Colors.white,
                onPressed: () {
                  SystemChrome.setPreferredOrientations([
                    DeviceOrientation.portraitUp,
                  ]).then((value) => Navigator.pop(context));
                },
              ),
            )
          ],
        ),
      ), onWillPop: () async {
        return SystemChrome.setPreferredOrientations([
          DeviceOrientation.portraitUp,
        ]).then((value) {
          Navigator.pop(context);
          return true;
        });
      })
    );
  }

  @override
  void dispose() {
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
    ]);
    super.dispose();
  }
}
