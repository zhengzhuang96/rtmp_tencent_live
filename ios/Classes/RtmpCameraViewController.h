//
//  RtmpCameraViewController.h
//  Pods-Runner
//
//  Created by zhengzhuang on 2020/6/6.
//

#import <Foundation/Foundation.h>
#import "TXLiteAVSDK_Professional/TXLiteAVSDK.h"

NS_ASSUME_NONNULL_BEGIN

@interface RtmpCameraViewController : UIViewController
@property(nonatomic,strong)TXLivePush* pusher;
- (instancetype)initWithDic:(NSDictionary *)dic;
@end

NS_ASSUME_NONNULL_END
