/*
 * @Author: zhengzhuang
 * @Date: 2020-06-09 11:39:19
 * @LastEditors: zhengzhuang
 * @LastEditTime: 2020-08-15 11:24:56
 * @Description: 直播
 * @FilePath: /rtmp_tencent_live/example/lib/main.dart
 */
import 'package:flutter/material.dart';
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';
import 'live_pusher.dart';
import 'live_video.dart';

void main() async {
  runApp(MyApp());
  await TencentLive.instance.init(
      licenseUrl: 'http://license.vod2.myqcloud.com/license/v1/3398497c80bc447ed493826c2b45f333/TXLiveSDK.licence',
      licenseKey: 'c095e4c1b94e61d5dda523bcb3b080c9'
  );
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: ListPage(),
      routes: {
        '/livePusher': (BuildContext context) => LivePusher(),
        '/liveVideo': (BuildContext context) => LiveVideo(),
      },
    );
  }
}

class ListPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('腾讯直播'),
      ),
      body: Container(
        child: Center(
          child: ListView(
            children: <Widget>[
              ListTile(
                title: Text('摄像头直播'),
                trailing: Icon(Icons.arrow_right),
                onTap: () => Navigator.pushNamed(context, '/livePusher'),
              ),
              ListTile(
                title: Text('播放直播'),
                trailing: Icon(Icons.arrow_right),
                onTap: () => Navigator.pushNamed(context, '/liveVideo'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}