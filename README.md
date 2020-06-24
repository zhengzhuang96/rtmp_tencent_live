# rtmp_tencent_live

腾讯直播flutter插件

不懂得问题欢迎提issues，或者欢迎加入flutter腾讯直播，群聊号码：1128573542

### 安装

在工程 pubspec.yaml 中加入 dependencies

``` 
dependencies:
  rtmp_tencent_live:
    git:
      url: https://github.com/zhengzhuang96/rtmp_tencent_live.git
      ref: master
```

+ pub 集成

```
dependencies:
  rtmp_tencent_live: ^0.0.4
```

### 使用


```dart
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';

```

### 页面中显示直播

```dart
import 'package:flutter/services.dart';

MethodChannel _controller;

TencentLive(
    licenceURL: "",     /// URL
    licenceKey: "",     /// KEY
    rtmpURL: "",        /// 推流链接
    onCreated: (controller) {
        _controller = controller;
    }
)
```

### 使用命令

#### 翻转

```dart
_controller.invokeMethod('setSwitchCamera');
```

##### 打开后置灯光

```dart
_controller.invokeMethod('setTurnOnFlashLight');
```

##### 镜像模式

```dart
_controller.invokeMethod('setMirror', 1);
```


##### 磨皮

v: 磨皮级别：0-9，0:关闭，9最大

```dart
_controller.invokeMethod('setDermabrasion', v.toInt());
```

##### 美白

v: 美白级别：0-9，0:关闭，9最大

```dart
_controller.invokeMethod('setWhitening', v.toInt());
```

##### 红润

v: 红润级别：0-9，0:关闭，9最大

```dart
_controller.invokeMethod('setUpRuddy', v.toInt());
```