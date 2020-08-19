//
//  VideoViewController.m
//  Pods-Runner
//
//  Created by zhengzhuang on 2020/8/18.
//

#import "VideoViewController.h"

@interface VideoViewController ()

@end

@implementation VideoViewController


- (instancetype)initWithDic:(NSDictionary *)dic
{
    self = [super init];
    if (self) {
        
        self.txLivePlayer = [[TXLivePlayer alloc] init];

//        TXLivePushConfig *_config = [[TXLivePushConfig alloc] init];  // 一般情况下不需要修改默认 config
//        self.pusher = [[TXLivePush alloc] initWithConfig: _config]; // config 参数不能为空
        [self.txLivePlayer setupVideoWidget:CGRectMake(0, 0, 0, 0) containView:self.view insertIndex:0];
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
}

@end
