#import "RtmpTencentLivePlugin.h"
#import "RtmpTencentFactory.h"

@implementation RtmpTencentLivePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
//  FlutterMethodChannel* channel = [FlutterMethodChannel
//      methodChannelWithName:@"rtmp_tencent_live"
//            binaryMessenger:[registrar messenger]];
//  RtmpTencentLivePlugin* instance = [[RtmpTencentLivePlugin alloc] init];
//  [registrar addMethodCallDelegate:instance channel:channel];
  [registrar registerViewFactory:[[RtmpTencentFactory alloc] initWithMessager:registrar.messenger]withId:@"TencentLive"];
}

//- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
//  if ([@"getPlatformVersion" isEqualToString:call.method]) {
//    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
//  } else {
//    result(FlutterMethodNotImplemented);
//  }
//}

@end
