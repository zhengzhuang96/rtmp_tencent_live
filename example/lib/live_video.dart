/*
 * @Author: zhengzhuang
 * @Date: 2020-08-15 11:22:02
 * @LastEditors: zhengzhuang
 * @LastEditTime: 2020-08-19 14:39:32
 * @Description: 播放直播
 * @FilePath: /rtmp_tencent_live/example/lib/live_video.dart
 */
import 'package:flutter/material.dart';
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';
import 'package:rtmp_tencent_live/tencent_vider_live_controller.dart';
import 'package:rtmp_tencent_live_example/live_video_full.dart';

class LiveVideo extends StatefulWidget {
  @override
  _LiveVideoState createState() => _LiveVideoState();
}

class _LiveVideoState extends State<LiveVideo> {
  TencentViderLiveController _controller;

  bool _state = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('播放直播'),
      ),
      body: Container(
        width: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height,
        child: Column(
          children: <Widget>[
            Container(
              width: MediaQuery.of(context).size.width,
              height: 200.0,
              color: Color.fromARGB(100, 0, 0, 0),
              child: _state
                ? Hero(
                    tag: "player",
                    child: TencentVideoLive(
                      onCreated: (controller) async {
                        _controller = controller;
                        _controller.playStart('http://play.rundle.cn/live/1386756491641393921.flv');
                      },
                    ),
                  )
                : Container(
                    alignment: Alignment.center,
                    child: CircularProgressIndicator(),
                  ),
            ),
            Expanded(child: Wrap(
                  children: <Widget>[
                    MaterialButton(
                      child: Text('横屏'),
                      color: Colors.white54,
                      onPressed: () {
                        _controller.setRenderRotation('1');
                        Navigator.of(context).push(MaterialPageRoute(builder: (context) {
                          return LiveVideoFull();
                        }));
                      }),
                    MaterialButton(
                      child: Text('竖屏'),
                      color: Colors.white54,
                      onPressed: () {
                        _controller.setRenderRotation('2');
                      }),
                    MaterialButton(
                      child: Text('等比显示'),
                      color: Colors.white54,
                      onPressed: () {
                        _controller.setRenderMode('1');
                      }),
                    MaterialButton(
                      child: Text('铺满显示'),
                      color: Colors.white54,
                      onPressed: () {
                        _controller.setRenderMode('2');
                      }),
                    MaterialButton(
                      child: Text('暂停播放'),
                      color: Colors.white54,
                      onPressed: () {
                        _controller.pause();
                      }),
                    MaterialButton(
                      child: Text('恢复播放'),
                      color: Colors.white54,
                      onPressed: () {
                        _controller.resume();
                      }),
                    MaterialButton(
                      child: Text('视频截图'),
                      color: Colors.white54,
                      onPressed: () {
                        _controller.screenCapture();
                      }),
                  ],
                ),)
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    print('dart ====> 停止播放1');
    _controller.stopPlay();
    super.dispose();
  }
}