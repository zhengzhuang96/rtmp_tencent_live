# rtmp_tencent_live

A new Flutter plugin.

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