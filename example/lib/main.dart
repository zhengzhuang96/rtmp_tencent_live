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

  double value = 0;
  double value2 = 0;
  double value3 = 0;
  double value4 = 0;

  /// 按钮组建
  Widget _buttonList(Icon icons, String title, Function onClick) {
    return InkWell(
      child: Container(
        color: Color.fromARGB(100, 255, 255, 255),
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
                          top: 0,
                          left: 0,
                          child: Container(
                            padding: EdgeInsets.only(
                              left: 20.0,
                              right: 20.0,
                              bottom: 20.0,
                              top: 20.0,
                            ),
                            width: MediaQuery.of(context).size.width,
                            child: Column(
                              children: <Widget>[
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                                  children: <Widget>[
                                    _buttonList(Icon(Icons.ac_unit), '翻转', () {
                                      _controller.invokeMethod('setSwitchCamera');
                                    }),
                                    _buttonList(Icon(Icons.ac_unit), '打开后置灯光', () {
                                      _controller.invokeMethod('setTurnOnFlashLight');
                                    }),
                                    _buttonList(Icon(Icons.ac_unit), '镜像模式', () {
                                      _controller.invokeMethod('setMirror', 1);
                                    }),
                                  ],
                                ),
                                Text('磨皮'),
                                SliderTheme( //自定义风格
                                  data: SliderTheme.of(context).copyWith(
                                      activeTrackColor: Colors.pink, //进度条滑块左边颜色
                                      inactiveTrackColor: Colors.blue, //进度条滑块右边颜色
                                      thumbColor: Colors.yellow, //滑块颜色
                                      overlayColor: Colors.green, //滑块拖拽时外圈的颜色
                                      overlayShape: RoundSliderOverlayShape(//可继承SliderComponentShape自定义形状
                                        overlayRadius: 25, //滑块外圈大小
                                      ),
                                      thumbShape: RoundSliderThumbShape(//可继承SliderComponentShape自定义形状
                                        disabledThumbRadius: 15, //禁用是滑块大小
                                        enabledThumbRadius: 15, //滑块大小
                                      ),
                                      inactiveTickMarkColor: Colors.black,
                                      tickMarkShape: RoundSliderTickMarkShape(//继承SliderTickMarkShape可自定义刻度形状
                                        tickMarkRadius: 4.0,//刻度大小
                                      ),
                                      showValueIndicator: ShowValueIndicator.onlyForDiscrete,//气泡显示的形式
                                      valueIndicatorColor: Colors.red,//气泡颜色
                                      valueIndicatorShape: PaddleSliderValueIndicatorShape(),//气泡形状
                                      valueIndicatorTextStyle: TextStyle(color: Colors.black),//气泡里值的风格
                                      trackHeight: 10 //进度条宽度
                                  ),
                                  child: Slider(
                                    value: value,
                                    onChanged: (v) {
                                      setState(() =>  value = v);
                                      _controller.invokeMethod('setDermabrasion', v.toInt());
                                    },
                                    label: "磨皮:$value",//气泡的值
                                    divisions: 10, //进度条上显示多少个刻度点
                                    max: 10,
                                    min: 0,
                                  ),
                                ),
                                Text('美白'),
                                SliderTheme( //自定义风格
                                  data: SliderTheme.of(context).copyWith(
                                      activeTrackColor: Colors.pink, //进度条滑块左边颜色
                                  ),
                                  child: Slider(
                                    value: value2,
                                    onChanged: (v) {
                                      setState(() =>  value2 = v);
                                      _controller.invokeMethod('setWhitening', v.toInt());
                                    },
                                    label: "美白:$value2",//气泡的值
                                    divisions: 10, //进度条上显示多少个刻度点
                                    max: 10,
                                    min: 0,
                                  ),
                                ),
                                Text('红润'),
                                SliderTheme( //自定义风格
                                  data: SliderTheme.of(context).copyWith(
                                      activeTrackColor: Colors.pink, //进度条滑块左边颜色
                                  ),
                                  child: Slider(
                                    value: value3,
                                    onChanged: (v) {
                                      setState(() =>  value3 = v);
                                      _controller.invokeMethod('setUpRuddy', v.toInt());
                                    },
                                    label: "红润:$value3",//气泡的值
                                    divisions: 10, //进度条上显示多少个刻度点
                                    max: 10,
                                    min: 0,
                                  ),
                                ),
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
