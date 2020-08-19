package com.example.rtmp_tencent_live;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;
import static android.content.ContentValues.TAG;

public class VideoLive implements PlatformView, MethodChannel.MethodCallHandler {

    private TXCloudVideoView mView;
    private TXLivePlayer mLivePlayer;
    private Context mContext;

    @SuppressLint("ResourceType")
    VideoLive(Context context, BinaryMessenger messenger, int id, Map<String, Object> params) {
        mContext = context;
        //mPlayerView 即 step1 中添加的界面 view
        mView = (TXCloudVideoView) LayoutInflater.from(context).inflate(R.layout.video_view, null);

        //创建 player 对象
        mLivePlayer = new TXLivePlayer(context);

        //关键 player 对象与界面 view
        mLivePlayer.setPlayerView(mView);

        Log.i("MyActivity","rtmptencentvideolive_" + id);
        MethodChannel methodChannel = new MethodChannel(messenger, "rtmptencentvideolive_" + id);
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        Log.i("MyActivity","校验MyClass.getView() - get item number");
        Map<String, Object> request = (Map<String, Object>) methodCall.arguments;

        switch (methodCall.method) {
            case "playStart":
                playStart(request);
                break;
            case "stopPlay":
                stopPlay();
                break;
            case "setRenderRotation":
                setRenderRotation(request);
                break;
            case "setRenderMode":
                setRenderMode(request);
                break;
            case "pause":
                pause();
                break;
            case "resume":
                resume();
                break;
            case "screenCapture":
                screenCapture();
            default:
                result.notImplemented();
        }
    }

    /// 开始直播
    protected void playStart(Map<String, Object> request) {
        String flvUrl = request.get("playUrl").toString();
        Log.i(TAG, "开始拉流播放---" + flvUrl);
        mLivePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐 FLV
    }

    /// 设置画面渲染方向
    protected void setRenderRotation(Map<String, Object> request) {
        String direction = request.get("direction").toString();
        Log.i("设置画面渲染方向", direction);
        if(direction.equals("1")) {
            Log.i("设置画面渲染方向", "1");
            mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);    // 正常播放（Home 键在画面正下方）
        } if(direction.equals("2")) {
            Log.i("设置画面渲染方向", "2");
            mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE);   // 画面顺时针旋转 270 度（Home 键在画面正左方）
        }
    }

    /// 暂停播放
    protected void pause() {
        mLivePlayer.pause();
    }

    /// 恢复播放
    protected void resume() {
        mLivePlayer.resume();
    }

    /// 关闭当前直播,停止播放
    protected void stopPlay() {
        mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
        mView.onDestroy();
    }

    /// 设置播放渲染模式
    protected void setRenderMode(Map<String, Object> request) {
        String renderMode = request.get("renderMode").toString();
        if(renderMode.equals("1")) {   // 等比
            mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);  // 将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
        } else if (renderMode.equals("2")) {    // 铺满
            mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);  // 将图像等比例铺满整个屏幕，多余部分裁剪掉，此模式下画面不会留黑边，但可能因为部分区域被裁剪而显示不全。
        }
    }

    /// 视频截图
    protected void screenCapture() {
        mLivePlayer.snapshot(new TXLivePlayer.ITXSnapshotListener() {
            @Override
            public void onSnapshot(Bitmap bmp) {
                if (null != bmp) {
                    Log.i("Image 图片=====>>>>>>", bmp.toString());
                    //获取到截图 bitmap
                    /// 指定我们想要存储文件的地址
                    String TargetPath = mContext.getFilesDir() + "";
                    Log.i("存储 Bitmap", "Save Path=" + TargetPath);
                    Log.i("判断指定文件夹的路径是否存在", "Save Path=" + !fileIsExist(TargetPath));
                    // 判断指定文件夹的路径是否存在
                    if(!fileIsExist(TargetPath)) {
                        Log.i("save bitmap", "123123");
                    } else {
                        // 如果指定文件夹创建成功，那么我们则需要进行图片存储操作
                        String name = System.currentTimeMillis() + ".jpg";
                        File saveFile = new File(TargetPath, name);
                        try {
                            FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                            Log.i("saveImgOut", "saveImgOut=" + saveImgOut);
                            // compress - 压缩的意思
                            bmp.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
                            // 存储完成后需要清楚相关的进程
                            saveImgOut.flush();
                            saveImgOut.close();
                            Log.i("save success", "saveImgOut=success");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // 其次把文件插入到系统图库
                        try {
                            MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                                    saveFile.getAbsolutePath(), name, null);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        // 最后通知图库更新
                        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + TargetPath)));
                    }
                }
            }
        });
    }

    public static boolean fileIsExist(String fileName) {
        // 传入指定的路径，然后判断路径是否存在
        File file = new File(fileName);
        if(file.exists()) {
            return true;
        } else {
            // file.mkdirs() 创建文件夹的意思
            return file.mkdirs();
        }
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void dispose() {
        mLivePlayer.stopPlay(true); // true 代表清除最后一帧画面
        mView.onDestroy();
    }
}
