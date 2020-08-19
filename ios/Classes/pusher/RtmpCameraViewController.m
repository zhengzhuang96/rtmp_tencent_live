//
//  RtmpCameraViewController.m
//  Pods-Runner
//
//  Created by zhengzhuang on 2020/6/6.
//

#import "RtmpCameraViewController.h"

@interface RtmpCameraViewController ()

@end

@implementation RtmpCameraViewController


- (instancetype)initWithDic:(NSDictionary *)dic
{
    self = [super init];
    if (self) {
        // Do any additional setup after loading the view.
//      NSString * const licenceURL = @"http://license.vod2.myqcloud.com/license/v1/3398497c80bc447ed493826c2b45f333/TXLiveSDK.licence";
//        NSString * const licenceKey = @"c095e4c1b94e61d5dda523bcb3b080c9";
//         NSString * const licenceURL = dic[@"licenceURL"];
//         NSString * const licenceKey = dic[@"licenceKey"];
//
        // TXLiveBase 位于 "TXLiveBase.h" 头文件中
        
        
        TXLivePushConfig *_config = [[TXLivePushConfig alloc] init];  // 一般情况下不需要修改默认 config

        self.pusher = [[TXLivePush alloc] initWithConfig: _config]; // config 参数不能为空
        
        [self.pusher startPreview:self.view];
        
        // 启动推流
//        NSString* rtmpUrl = @"rtmp://push.rundle.cn/live/29?txSecret=a09d849fe9ca92f9692affcf263c9388&txTime=5EE0462E";
//        NSString* rtmpUrl = dic[@"rtmpURL"];
//        // 此处填写您的 rtmp 推流地址
//        int i = [self.pusher startPush:rtmpUrl];
//        NSLog(@"1111111111---    %d",i);
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
}

@end
