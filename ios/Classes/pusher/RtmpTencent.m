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
    BOOL _islight;
    BOOL _ismirror;
    
    float _beauty;
    float _whiten;
    float _ruddiness;
}

- (id)initWithFrame:(CGRect)frame viewId:(NSString *)viewId args:(id)args binaryMessager:(NSObject<FlutterBinaryMessenger> *)messager
{
    if (self = [super init])
    {
        _frame = frame;
        _viewId = viewId;
        _args = args;
        _islight = NO;
        _ismirror = NO;
        _beauty = 0;
        _whiten = 0;
        _ruddiness = 0;
    }

    FlutterMethodChannel* _channel = [FlutterMethodChannel methodChannelWithName:[NSString stringWithFormat:@"rtmptencentlivepush_%@",viewId] binaryMessenger:messager];
    
    __weak typeof (self)WealSelf = self;
    [_channel setMethodCallHandler:^(FlutterMethodCall * _Nonnull call, FlutterResult  _Nonnull result) {
        [WealSelf onMethodCall:call result:result];
    }];
    camera = [[RtmpCameraViewController alloc]initWithDic:(NSDictionary *)args];
    return self;
}

-(void)onMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result{
    NSLog(@"---- 监听 ----");
    if ([call.method isEqualToString:@"setTurnOnFlashLight"]) {
        if (!_islight) {
            [camera.pusher toggleTorch:YES];
        }else{
            [camera.pusher toggleTorch:NO];
        }
        _islight = !_islight;
        NSLog(@"打开后置灯光");
    }
    if ([call.method isEqualToString:@"setSwitchCamera"]) {
        NSLog(@"切换前后摄像头");
        [camera.pusher switchCamera];
    }
    if ([call.method isEqualToString:@"setMirror"]) {
        NSLog(@"镜像");
        if (!_ismirror) {
            [camera.pusher setMirror:YES];
        }else{
            [camera.pusher setMirror:NO];
        }
        _ismirror = !_ismirror;
    }
    if ([call.method isEqualToString:@"setDermabrasion"]) {
        NSDictionary *info = call.arguments;
        _beauty = [info[@"val"] floatValue];
        [self mysetbeautystyle];
        NSLog(@"磨皮 ----- %@",info);
    }
    if ([call.method isEqualToString:@"setWhitening"]) {
        NSDictionary *info = call.arguments;
        _whiten = [info[@"val"] floatValue];
        [self mysetbeautystyle];
    }
    if ([call.method isEqualToString:@"setUpRuddy"]) {
        NSDictionary *info = call.arguments;
        _ruddiness = [info[@"val"] floatValue];
        [self mysetbeautystyle];
    }
    if ([call.method isEqualToString:@"startLive"]) {
       NSLog(@"推流 ");
       NSString* rtmpUrl = _args[@"rtmpURL"];
       // 此处填写您的 rtmp 推流地址
       NSNumber *i = [NSNumber numberWithInt:[camera.pusher startPush:rtmpUrl]];
       NSLog(@"startPush res from sdk: %@",i);
       result(i);
    }
}

-(void)mysetbeautystyle {
    [camera.pusher setBeautyStyle:0 beautyLevel:_beauty whitenessLevel:_whiten ruddinessLevel:_ruddiness];
}

- (UIView *)view
{
    return camera.view;
}

@end
