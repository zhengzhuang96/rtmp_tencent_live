import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
//    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
//  Future<void> initPlatformState() async {
//    String platformVersion;
//    // Platform messages may fail, so we use a try/catch PlatformException.
//    try {
//      platformVersion = await Rtmptencent.platformVersion;
//    } on PlatformException {
//      platformVersion = 'Failed to get platform version.';
//    }
//
//    // If the widget was removed from the tree while the asynchronous platform
//    // message was in flight, we want to discard the reply rather than calling
//    // setState to update our non-existent appearance.
//    if (!mounted) return;
//
//    setState(() {
//      _platformVersion = platformVersion;
//    });
//  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: LivePage(),
    );
  }
}

class LivePage extends StatefulWidget {
  @override
  _LivePageState createState() => _LivePageState();
}

class _LivePageState extends State<LivePage> {
  MethodChannel _controller;

  /// 按钮组建
  Widget _buttonList(Icon icons, String title, Function onClick) {
    return InkWell(
      child: Container(
        child: Column(
          children: <Widget>[
            icons,
            Text('$title'),
          ],
        ),
      ),
      onTap: onClick,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        width: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height,
        child: Stack(
          children: <Widget>[
            TencentLive(
              licenceURL: "http://license.vod2.myqcloud.com/license/v1/3398497c80bc447ed493826c2b45f333/TXLiveSDK.licence",
              licenceKey: "c095e4c1b94e61d5dda523bcb3b080c9",
              rtmpURL: "rtmp://push.rundle.cn/live/29?txSecret=a09d849fe9ca92f9692affcf263c9388&txTime=5EE0462E",
              onCreated: (controller) {
                _controller = controller;
              }
            ),
            Positioned(
              top: 0,
              left: 0,
              child: Container(
                width: MediaQuery.of(context).size.width,
                height: MediaQuery.of(context).size.height,
//                color: Colors.amber,
                child: SafeArea(
                    child: Stack(
                      children: <Widget>[
                        Text('data'),
                        Positioned(
                          bottom: 0,
                          left: 0,
                          child: Container(
                            padding: EdgeInsets.only(
                              left: 20.0,
                              right: 20.0,
                              bottom: 20.0,
                              top: 20.0,
                            ),
                            width: MediaQuery.of(context).size.width,
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceAround,
                              children: <Widget>[
                                _buttonList(Icon(Icons.ac_unit), '翻转', () {
                                  _controller.invokeMethod('setSwitchCamera');
                                }),
                                _buttonList(Icon(Icons.ac_unit), '打开后置灯光', () {
                                  _controller.invokeMethod('setTurnOnFlashLight');
                                }),
                                _buttonList(Icon(Icons.ac_unit), '美颜', () {
                                  _controller.invokeMethod('setBeautyStyle', 1);
                                }),
                                _buttonList(Icon(Icons.ac_unit), '镜像模式', () {
                                  _controller.invokeMethod('setMirror', 1);
                                }),
                              ],
                            ),
                          ),
                        )
                      ],
                    )
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
