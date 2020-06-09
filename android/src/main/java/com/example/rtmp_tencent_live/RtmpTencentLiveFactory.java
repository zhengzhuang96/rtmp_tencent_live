package com.example.rtmp_tencent_live;

import android.content.Context;

import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class RtmpTencentLiveFactory extends PlatformViewFactory {
    private final BinaryMessenger messenger;

    public RtmpTencentLiveFactory(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
    }

    @Override
    public PlatformView create(Context context, int id, Object args) {
        Map<String, Object> params = (Map<String, Object>) args;
        return new RtmpTencentLive(context, messenger, id, params);
    }
}
