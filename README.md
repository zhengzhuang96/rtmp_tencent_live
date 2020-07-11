# rtmp_tencent_live

腾讯直播flutter插件

有问题欢迎提issues，或者欢迎加入flutter腾讯直播，群聊号码：1128573542

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

### 导入


```dart
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';

```



## 注册license

```
void main() async {
  await TencentLive.instance.init(
      licenseUrl: 'http://******.licence', 
      licenseKey: '******'
  );
}
```



### 页面中显示直播

```dart
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';
import 'package:rtmp_tencent_live/tencent_live_push_Controller.dart';

TencentLivePushController _controller;

TencentLive(
    rtmpURL: "",        /// 推流链接
    onCreated: (controller) {
        _controller = controller;
    }
)
```

### 使用命令

|              | 支持度 |          使用方法          |              介绍               |
| :----------: | :----: | :------------------------: | :-----------------------------: |
|   开始直播   |   ✅    |        startLive()         |                                 |
|  翻转摄像头  |   ✅    |     setSwitchCamera()      |                                 |
| 打开后置灯光 |   ✅    |   setTurnOnFlashLight()    |   只有在后置摄像头状态才可用    |
|   镜像模式   |   ✅    |        setMirror()         |         用户端显示状况          |
|     磨皮     |   ✅    | setDermabrasion(v.toInt()) | v: 磨皮级别：0-9，0:关闭，9最大 |
|     美白     |   ✅    |  setWhitening(v.toInt())   | v: 美白级别：0-9，0:关闭，9最大 |
|     红润     |   ✅    |   setUpRuddy(v.toInt())    | v: 红润级别：0-9，0:关闭，9最大 |

