#import "RtmpTencentLivePlugin.h"
#import "RtmpTencentFactory.h"
#import "TXLiteAVSDK_Professional/TXLiteAVSDK.h"

@implementation RtmpTencentLivePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"rtmp_tencent_live_flutter"
            binaryMessenger:[registrar messenger]];
  RtmpTencentLivePlugin* instance = [[RtmpTencentLivePlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
    
  [registrar registerViewFactory:[[RtmpTencentFactory alloc] initWithMessager:registrar.messenger]withId:@"TencentLive"];
    
    
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"setLicence" isEqualToString:call.method]) {
      NSDictionary *info = call.arguments;
      NSLog(@"qewqe");
      [TXLiveBase setLicenceURL:info[@"url"] key:info[@"key"]];
      NSLog(@"SDK Version = %@", [TXLiveBase getSDKVersionStr]);
//    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  }
}

@end
