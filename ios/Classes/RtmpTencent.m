//
//  RtmpTencent.m
//  Pods-Runner
//
//  Created by zhengzhuang on 2020/6/6.
//

#import "RtmpTencent.h"
#import <CoreGraphics/CoreGraphics.h>
#import "TXLiteAVSDK_Professional/TXLiteAVSDK.h"
#import "RtmpCameraViewController.h"

@implementation RtmpTencent
{
    CGRect _frame;
    NSString* _viewId;
    id _args;
    UILabel *_subLabel;
    FlutterMethodChannel* _channel;
    RtmpCameraViewController *camera;
}

- (id)initWithFrame:(CGRect)frame viewId:(NSString *)viewId args:(id)args binaryMessager:(NSObject<FlutterBinaryMessenger> *)messager
{
    if (self = [super init])
    {
        _frame = frame;
        _viewId = viewId;
        _args = args;
    }

    FlutterMethodChannel* _channel = [FlutterMethodChannel methodChannelWithName:[NSString stringWithFormat:@"tencentlive_%@",viewId] binaryMessenger:messager];
    
    __weak typeof (self)WealSelf = self;
    [_channel setMethodCallHandler:^(FlutterMethodCall * _Nonnull call, FlutterResult  _Nonnull result) {
        [WealSelf onMethodCall:call result:result];
    }];
    camera = [[RtmpCameraViewController alloc]initWithDic:(NSDictionary *)args];
    return self;
}

-(void)onMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result{
    NSLog(@"---- 监听 ----");
    if ([call.method isEqualToString:@"setText"]) {
        NSLog(@"qqqqqq");
    }
    if ([call.method isEqualToString:@"setTurnOnFlashLight"]) {
        NSLog(@"打开后置灯光");
    }
    if ([call.method isEqualToString:@"setBeautyStyle"]) {
        NSLog(@"美颜");
    }
    if ([call.method isEqualToString:@"setSwitchCamera"]) {
        NSLog(@"美颜");
        [camera.pusher switchCamera];
    }
}

- (UIView *)view
{
    return camera.view;
}

@end
