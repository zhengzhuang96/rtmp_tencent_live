# rtmp_tencent_live
[![pub package](https://img.shields.io/pub/v/rtmp_tencent_live.svg)](https://pub.flutter-io.cn/packages/rtmp_tencent_live)

腾讯直播flutter插件

承接各种前端项目，flutter App项目<br />
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
  rtmp_tencent_live: ^0.0.8
```

### iOS 
info.plist 文件配置
```
<key>NSPhotoLibraryAddUsageDescription</key>
<string>请求使用相册</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>请求使用相册</string>
<key>io.flutter.embedded_views_preview</key>
<true/>
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

## 推流

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


## 拉流

```dart
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';
import 'package:rtmp_tencent_live/tencent_vider_live_controller.dart';

TencentViderLiveController _controller;

TencentVideoLive(
  onCreated: (controller) async {
    _controller = controller;
    _controller.playStart('http://************.flv');
    _controller.setRenderRotation('1');
  },
)
```

### 使用命令

|              | 支持度  |           使用方法          |              介绍            |
| :----------: | :----: | :------------------------: | :-------------------------: |
|   开始播放    |   ✅   |        playStart()         |       直接传入播放地址         |
|   横屏/竖屏   |   ✅   |     setRenderRotation()    |       1: 横屏，2：竖屏         |
|   等比显示    |   ✅   |       setRenderMode()      |    1: 等比显示，2：铺满显示     |
|   暂停播放    |   ✅   |          pause()           |                              |
|   恢复播放    |   ✅   |          resume()          |                              |
|   视频截图    |   ✅   |       screenCapture()      |       视频截图保存到本地        |
|   停止播放    |   ✅   |         stopPlay()         |                              |

### 如果你喜欢这个插件，可以请作者喝杯咖啡

<div style="display: float;">
  <img src="https://github.com/zhengzhuang96/rtmp_tencent_live/blob/master/assets/alipay.jpeg" width="200" alt="支付宝"/>
  <img src="https://github.com/zhengzhuang96/rtmp_tencent_live/blob/master/assets/wxapy.jpeg" width="200" alt="微信"/>
</div>