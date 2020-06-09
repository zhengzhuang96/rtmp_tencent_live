//
//  RtmpTencentFactory.m
//  Pods-Runner
//
//  Created by zhengzhuang on 2020/6/6.
//

#import "RtmpTencentFactory.h"
#import "RtmpTencent.h"

@implementation RtmpTencentFactory{
    NSObject<FlutterBinaryMessenger>*_messager;
}
- (instancetype)initWithMessager:(NSObject<FlutterBinaryMessenger>*)messager
{
    self = [super init];
    if (self) {
        _messager = messager;
    }
    return self;
}
- (NSObject <FlutterPlatformView> *)createWithFrame:(CGRect)frame viewIdentifier:(int64_t)viewId arguments:(id _Nullable)args
{
    RtmpTencent *myPlatformViewObject = [[RtmpTencent alloc] initWithFrame:frame viewId:[NSString stringWithFormat:@"%lld",viewId] args:args binaryMessager:_messager];
    return myPlatformViewObject;
}
- (NSObject<FlutterMessageCodec>*)createArgsCodec{
    return [FlutterStandardMessageCodec sharedInstance];
}
@end
