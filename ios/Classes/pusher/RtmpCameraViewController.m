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
        
        TXLivePushConfig *_config = [[TXLivePushConfig alloc] init];  // 一般情况下不需要修改默认 config
        self.pusher = [[TXLivePush alloc] initWithConfig: _config]; // config 参数不能为空
        [self.pusher startPreview:self.view];
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
}

@end
