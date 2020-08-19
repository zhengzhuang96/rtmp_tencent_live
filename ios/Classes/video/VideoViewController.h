//
//  VideoViewController.h
//  Pods-Runner
//
//  Created by zhengzhuang on 2020/8/18.
//

#import <Foundation/Foundation.h>
#import "TXLiteAVSDK_Professional/TXLiteAVSDK.h"

NS_ASSUME_NONNULL_BEGIN

@interface VideoViewController : UIViewController
@property(nonatomic,strong)TXLivePlayer* txLivePlayer;
- (instancetype)initWithDic:(NSDictionary *)dic;
@end

NS_ASSUME_NONNULL_END
