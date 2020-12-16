
import 'package:flutter/material.dart';
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';
import 'package:rtmp_tencent_live/tencent_live_push_Controller.dart';

class LivePusher extends StatefulWidget {
  @override
  _LivePusherState createState() => _LivePusherState();
}

class _LivePusherState extends State<LivePusher> {
  TencentLivePushController _controller;

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
      appBar: AppBar(
        title: Text('摄像头直播'),
      ),
      body: Container(
        width: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height,
        child: Stack(
          children: <Widget>[
            TencentLiveView(
              rtmpURL: "rtmp://push.rundle.cn/live/1?txSecret=43f6e75373b542a9d4cfce11da1765cf&txTime=5F4F67B3",
              onCreated: (controller) async {
                _controller = controller;
              }
            ),
            Positioned(
              top: 0,
              left: 0,
              child: Container(
                width: MediaQuery.of(context).size.width,
                height: MediaQuery.of(context).size.height,
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
                                      _controller.setSwitchCamera();
                                    }),
                                    _buttonList(Icon(Icons.ac_unit), '打开后置灯光', () {
                                      _controller.setTurnOnFlashLight();
                                    }),
                                    _buttonList(Icon(Icons.ac_unit), '镜像模式', () {
                                      _controller.setMirror();
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
                                      _controller.setDermabrasion(v.toInt());
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
                                      _controller.setWhitening(v.toInt());
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
                                      _controller.setUpRuddy(v.toInt());
                                    },
                                    label: "红润:$value3",//气泡的值
                                    divisions: 10, //进度条上显示多少个刻度点
                                    max: 10,
                                    min: 0,
                                  ),
                                ),
                                MaterialButton(
                                  child: Text("开始直播", style: TextStyle(
                                    color: Colors.white,
                                    fontSize: 20.0
                                  )),
                                  color: Colors.blue,
                                  onPressed: () => _controller.startLive(),
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
