# rtmp_tencent_live

腾讯直播flutter插件

不懂得问题欢迎提issues，或者欢迎加入flutter腾讯直播，群聊号码：1128573542

## Getting Started


MethodChannel _controller;


TencentLive(
    licenceURL: "",
    licenceKey: "",
    rtmpURL: "",
    onCreated: (controller) {
    _controller = controller;
    }
),

##### 翻转
_controller.invokeMethod('setSwitchCamera');

##### 打开后置灯光
_controller.invokeMethod('setTurnOnFlashLight');

##### 镜像模式
_controller.invokeMethod('setMirror', 1);

##### 磨皮  
_controller.invokeMethod('setDermabrasion', v.toInt());

##### 美白  
_controller.invokeMethod('setWhitening', v.toInt());

##### 红润
_controller.invokeMethod('setUpRuddy', v.toInt());