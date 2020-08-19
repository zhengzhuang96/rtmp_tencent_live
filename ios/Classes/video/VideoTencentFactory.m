//
//  VideoTencentFactory.m
//  Pods-Runner
//
//  Created by zhengzhuang on 2020/8/18.
//

#import "VideoTencentFactory.h"
#import "VideoTencent.h"

@implementation VideoTencentFactory{
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
    VideoTencent *myPlatformViewObject = [[VideoTencent alloc] initWithFrame:frame viewId:[NSString stringWithFormat:@"%lld",viewId] args:args binaryMessager:_messager];
    return myPlatformViewObject;
}
- (NSObject<FlutterMessageCodec>*)createArgsCodec{
    return [FlutterStandardMessageCodec sharedInstance];
}
@end
