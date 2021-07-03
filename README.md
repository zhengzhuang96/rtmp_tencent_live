English | [简体中文](./README.zh-CN.md)
# rtmp_tencent_live
[![pub package](https://img.shields.io/pub/v/rtmp_tencent_live.svg)](https://pub.flutter-io.cn/packages/rtmp_tencent_live)

Tencent live flutter plug in

If you have any questions, please mention issues, or join the live broadcast of flitter Tencent, QQ group: 1128573542

### install

Adding dependencies in pubspec.yaml

``` 
dependencies:
  rtmp_tencent_live:
    git:
      url: https://github.com/zhengzhuang96/rtmp_tencent_live.git
      ref: master
```

+ pub integration

```
dependencies:
  rtmp_tencent_live: ^0.0.8
```

### iOS 
info.plist File configuration
```
<key>NSPhotoLibraryAddUsageDescription</key>
<string>Request to use photo album</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>Request to use photo album</string>
<key>io.flutter.embedded_views_preview</key>
<true/>
```

### Import


```dart
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';
```

## Registration license

```
void main() async {
  await TencentLive.instance.init(
      licenseUrl: 'http://******.licence', 
      licenseKey: '******'
  );
}
```

## Push flow

### The live broadcast is displayed on the page

```dart
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';
import 'package:rtmp_tencent_live/tencent_live_push_Controller.dart';

TencentLivePushController _controller;

TencentLive(
    rtmpURL: "",        /// Push stream link
    onCreated: (controller) {
        _controller = controller;
    }
)
```

### Using commands

|                         | Support |          usage method     |                     introduce               |
| :---------------------: | :-----: | :-----------------------: | :-----------------------------------------: |
| Live broadcast          |   ✅    |        startLive()         |                                            |
|  Flip camera            |   ✅    |     setSwitchCamera()      |                                            |
| Turn on the rear lights |   ✅    |   setTurnOnFlashLight()    |   Only available in the rear camera state  |
|   Mirror mode           |   ✅    |        setMirror()         |         Client display status              |
|     Moulting            |   ✅    | setDermabrasion(v.toInt()) | v: Grinding level: 0-9, 0: off, 9: max     |
|     skin whitening      |   ✅    |  setWhitening(v.toInt())   | v: Whitening level: 0-9, 0: off, 9 max     |
|     Ruddy               |   ✅    |   setUpRuddy(v.toInt())    | v: Ruddy level: 0-9, 0: off, 9: maximum    |


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

### Using commands

|                                  | Support|       usage method        |              introduce                        |
| :------------------------------: | :----: | :-----------------------: | :-------------------------: |
|          Start playing           |   ✅   |        playStart()         |       Direct incoming playback address       |
|   Horizontal / vertical screen   |   ✅   |     setRenderRotation()    |  1: Horizontal screen, 2: vertical screen    |
|      Proportional display        |   ✅   |       setRenderMode()      |  1: Proportional display, 2: full display    |
|           Pause play             |   ✅   |          pause()           |                                              |
|         Resume playback          |   ✅   |          resume()          |                                              |
|           Video capture          |   ✅   |       screenCapture()      |       Save video capture to local            |
|          stop playing            |   ✅   |         stopPlay()         |                                              |

### If you like this plugin, you can buy the author a cup of coffee

<div style="display: float;">
  <img src="https://github.com/zhengzhuang96/rtmp_tencent_live/blob/master/assets/alipay.jpeg" width="200" alt="支付宝"/>
  <img src="https://github.com/zhengzhuang96/rtmp_tencent_live/blob/master/assets/wxapy.jpeg" width="200" alt="微信"/>
</div>