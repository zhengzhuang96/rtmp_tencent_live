import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:rtmp_tencent_live/rtmp_tencent_live.dart';

void main() {
  const MethodChannel channel = MethodChannel('rtmp_tencent_live');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    // expect(await RtmpTencentLive.platformVersion, '42');
  });
}
