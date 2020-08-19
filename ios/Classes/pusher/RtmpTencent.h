//
//  RtmpTencent.h
//  Pods-Runner
//
//  Created by zhengzhuang on 2020/6/6.
//

#import <Foundation/Foundation.h>
#import <Flutter/Flutter.h>

NS_ASSUME_NONNULL_BEGIN

@interface RtmpTencent : NSObject <FlutterPlatformView>
- (id)initWithFrame:(CGRect)frame viewId:(NSString *)viewId args:(id)args binaryMessager:(NSObject<FlutterBinaryMessenger>*)messager;
@end

NS_ASSUME_NONNULL_END
