//
//  VideoTencent.m
//  Pods-Runner
//
//  Created by zhengzhuang on 2020/8/18.
//

#import "VideoTencent.h"
#import <CoreGraphics/CoreGraphics.h>
#import "TXLiteAVSDK_Professional/TXLiteAVSDK.h"
#import "VideoViewController.h"
#import <AssetsLibrary/AssetsLibrary.h>

@implementation VideoTencent
{
    CGRect _frame;
    NSString* _viewId;
    id _args;
    UILabel *_subLabel;
    FlutterMethodChannel* _channel;
    VideoViewController *video;
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
        _beauty = 0;
        _whiten = 0;
        _ruddiness = 0;
    }

    FlutterMethodChannel* _channel = [FlutterMethodChannel methodChannelWithName:[NSString stringWithFormat:@"rtmptencentvideolive_%@",viewId] binaryMessenger:messager];
    
    __weak typeof (self)WealSelf = self;
    [_channel setMethodCallHandler:^(FlutterMethodCall * _Nonnull call, FlutterResult  _Nonnull result) {
        [WealSelf onMethodCall:call result:result];
    }];
    video = [[VideoViewController alloc]initWithDic:(NSDictionary *)args];
    return self;
}

-(void)onMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result{
    NSLog(@"---- 监听 ----");
    if ([call.method isEqualToString:@"playStart"]) {
        NSDictionary* info = call.arguments;
        NSString* _flvUrl = info[@"playUrl"];
        [video.txLivePlayer startPlay:_flvUrl type:PLAY_TYPE_LIVE_FLV];
        NSLog(@"开始直播");
    }
    if ([call.method isEqualToString:@"setRenderRotation"]) {
        NSLog(@"设置画面旋转方向");
        NSDictionary* info = call.arguments;
        NSString* _dataInfo = info[@"direction"];
        if([_dataInfo isEqualToString:@"1"]) {
            [video.txLivePlayer setRenderRotation:HOME_ORIENTATION_DOWN];  // 正常播放（Home 键在画面正下方）
        } else if ([_dataInfo isEqualToString:@"2"]) {
            [video.txLivePlayer setRenderRotation:HOME_ORIENTATION_RIGHT];  // 画面顺时针旋转 270 度（Home 键在画面正左方）
        }
    }
    if ([call.method isEqualToString:@"setRenderMode"]) {
        NSLog(@"设置播放渲染模式");
        NSDictionary* _info = call.arguments;
        NSString* _dataInfo = _info[@"renderMode"];
        if([_dataInfo isEqualToString:@"1"]) {
            [video.txLivePlayer setRenderMode:RENDER_MODE_FILL_SCREEN]; // 将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
        } else if ([_dataInfo isEqualToString:@"2"]) {
            [video.txLivePlayer setRenderMode:RENDER_MODE_FILL_EDGE];   // 将图像等比例铺满整个屏幕，多余部分裁剪掉，此模式下画面不会留黑边，但可能因为部分区域被裁剪而显示不全。
        }
    }
    if([call.method isEqualToString:@"stopPlay"]) {
        NSLog(@"停止播放");
        // 停止播放
        [video.txLivePlayer stopPlay];
        [video.txLivePlayer removeVideoWidget]; // 记得销毁view控件
    }
    if([call.method isEqualToString:@"pause"]) {
        NSLog(@"暂停播放");
        // 暂停播放
        [video.txLivePlayer pause];
    }
    if([call.method isEqualToString:@"resume"]) {
        NSLog(@"暂停恢复");
        // 恢复
        [video.txLivePlayer resume];
    }
    if([call.method isEqualToString:@"screenCapture"]) {
        NSLog(@"视频截图");
        
        __block ALAssetsLibrary *lib = [[ALAssetsLibrary alloc] init];
        
        // 视频截图
        [video.txLivePlayer snapshot:^(TXImage *val) {
            NSLog(@"暂停恢复");
            [lib writeImageToSavedPhotosAlbum:val.CGImage metadata:nil completionBlock:^(NSURL *assetURL, NSError *error) {
                NSLog(@"assetURL = %@, error = %@", assetURL, error);
//                [myDict setValue:[NSString stringWithFormat:@"%@",assetURL] forKey:@"picAddress"];
                lib = nil;
            }];
        }];
    }
}

- (UIView *)view
{
    return video.view;
}

@end
