package com.example.rtmp_tencent_live.liveroom;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.example.rtmp_tencent_live.R;
import android.os.HandlerThread;

import com.example.rtmp_tencent_live.liveroom.roomutil.AnchorInfo;
import com.example.rtmp_tencent_live.liveroom.roomutil.LoginInfo;
import com.example.rtmp_tencent_live.liveroom.roomutil.MLVBCommonDef;
import com.tencent.liteav.audio.TXAudioEffectManager;
import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.liteav.beauty.TXBeautyManager;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.security.InvalidParameterException;
import org.json.JSONArray;
import java.util.Vector;

public class MLVBLiveRoomImpl extends MLVBLiveRoom {
    protected static final String           TAG                         = "MLVBLiveRoomImpl";
    protected static final int              STREAM_MIX_MODE_JOIN_ANCHOR = 0;
    protected static final int              LIVEROOM_ROLE_NONE          = 0;
    protected static final int              LIVEROOM_ROLE_PUSHER        = 1;
    protected static final int              STREAM_MIX_MODE_PK          = 1;

    protected HashMap<String, PlayerItem>   mPlayers                    = new LinkedHashMap<>();
    protected int                           mMixMode                    = STREAM_MIX_MODE_JOIN_ANCHOR;

    protected int                           mSelfRoleType               = LIVEROOM_ROLE_NONE;

    protected Context                       mAppContext                 = null;
    protected Handler                       mListenerHandler            = null;
    protected static MLVBLiveRoomImpl       mInstance                   = null;
    protected IMLVBLiveRoomListener         mListener                   = null;
    private Runnable                        mRequestPKTimeoutTask       = null;
//    protected HttpRequests                  mHttpRequest                = null;     //HTTP CGI请求相关

    protected StreamMixturer                mStreamMixturer;                        //混流类
    private AnchorInfo                      mPKAnchorInfo               = null;

    protected LoginInfo                     mSelfAccountInfo;
    protected TXLivePlayConfig              mTXLivePlayConfig;
    protected HeartBeatThread               mHeartBeatThread;                       //心跳

    protected boolean                       mJoinPusher                 = false;
    protected boolean                       mBackground                 = false;
    protected boolean                       mScreenAutoEnable           = true;

    protected String                        mCurrRoomID;
    protected String                        mSelfPushUrl;
    protected String                        mSelfAccelerateURL;
    protected TXLivePlayer                  mTXLivePlayer;

    private long                            mLastEnterAudienceTimeMS    = 0;
    private long                            mLastExitAudienceTimeMS     = 0;
    private long                            mTimeDiff                   = 0;        //客户端和服务器时间差，用户连麦和PK请求超时处理
    protected int                           mRoomStatusCode             = 0;

    protected TXLivePusher                  mTXLivePusher;
    protected TXLivePushListenerImpl        mTXLivePushListener;


    public MLVBLiveRoomImpl(Context context) {
        if (context == null) {
            throw new InvalidParameterException("MLVBLiveRoom init error：context can not null！");
        }
        mAppContext = context.getApplicationContext();
        mListenerHandler = new Handler(mAppContext.getMainLooper());
        mStreamMixturer = new StreamMixturer();
        mHeartBeatThread = new HeartBeatThread();

        mTXLivePlayConfig = new TXLivePlayConfig();
        mTXLivePlayer = new TXLivePlayer(context);
        mTXLivePlayConfig.setAutoAdjustCacheTime(true);
        mTXLivePlayConfig.setMaxAutoAdjustCacheTime(2.0f);
        mTXLivePlayConfig.setMinAutoAdjustCacheTime(2.0f);
        mTXLivePlayer.setConfig(mTXLivePlayConfig);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mTXLivePlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(final int event, final Bundle param) {
                if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                    String msg = mAppContext.getString(Integer.parseInt("拉流失败"), param.getString(TXLiveConstants.EVT_DESCRIPTION));
                    TXCLog.e(TAG, msg);
                    callbackOnThread(mListener, "onDebugLog", msg);
                    callbackOnThread(mListener, "onError", event, msg, param);
                } else if (event == TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION) {
                    int width = param.getInt(TXLiveConstants.EVT_PARAM1, 0);
                    int height = param.getInt(TXLiveConstants.EVT_PARAM2, 0);
                    if (width > 0 && height > 0) {
                        float ratio = (float) height / width;
                        //pc上混流后的宽高比为4:5，这种情况下填充模式会把左右的小主播窗口截掉一部分，用适应模式比较合适
                        if (ratio > 1.3f) {
                            mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                        } else {
                            mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
                        }
                    }
                }
            }

            private void callbackOnThread(IMLVBLiveRoomListener mListener, String onError, int event, String msg, Bundle param) {
            }

            private void callbackOnThread(IMLVBLiveRoomListener mListener, String onDebugLog, String msg) {
            }

            @Override
            public void onNetStatus(Bundle status) {

            }
        });
    }

    public static MLVBLiveRoom sharedInstance(Context context) {
        if (mInstance == null) {
            synchronized (MLVBLiveRoomImpl.class) {
                if (mInstance == null) {
                    mInstance = new MLVBLiveRoomImpl(context);
                }
            }
        }
        return mInstance;
    }

    public static void destroySharedInstance() {
        synchronized (MLVBLiveRoomImpl.class) {
            if (mInstance != null) {
                mInstance.destroy();
                mInstance = null;
            }
        }
    }

    @Override
    public void setListener(IMLVBLiveRoomListener listener) {

    }

    @Override
    public void setListenerHandler(Handler listenerHandler) {

    }

    @Override
    public void login(LoginInfo loginInfo, IMLVBLiveRoomListener.LoginCallback callback) {

    }

    @Override
    public void logout() {

    }

    @Override
    public void setSelfProfile(String userName, String avatarURL) {

    }

    @Override
    public void getRoomList(int index, int count, IMLVBLiveRoomListener.GetRoomListCallback callback) {

    }

    @Override
    public void getAudienceList(IMLVBLiveRoomListener.GetAudienceListCallback callback) {

    }

    /**
     * 创建房间（主播调用）
     * <p>
     * 主播开播的正常调用流程是：
     * 1.【主播】调用 startLocalPreview() 打开摄像头预览，此时可以调整美颜参数。
     * 2.【主播】调用 createRoom 创建直播间，房间创建成功与否会通过 {@link IMLVBLiveRoomListener.CreateRoomCallback} 通知给主播。
     *
     * @param roomID   房间标识，推荐做法是用主播的 userID 作为房间的 roomID，这样省去了后台映射的成本。room ID 可以填空，此时由后台生成。
     * @param roomInfo 房间信息（非必填），用于房间描述的信息，比如房间名称，允许使用 JSON 格式作为房间信息。
     * @param callback 创建房间的结果回调
     */
    @Override
    public void createRoom(final String roomID, final String roomInfo, final String pushURL, final IMLVBLiveRoomListener.CreateRoomCallback callback) {
        TXCLog.i(TAG, "API -> createRoom:" + roomID + ":" + roomInfo);
        mSelfRoleType = LIVEROOM_ROLE_PUSHER;

        if (mSelfAccountInfo == null) return;
        //1. 在应用层调用startLocalPreview，启动本地预览

//        final String pushURL = pushURL;
        mSelfPushUrl = pushURL;

        //3.开始推流
        startPushStream(pushURL, TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, new StandardCallback() {
            @Override
            public void onError(int errCode, String errInfo) {
                callbackOnThread(callback, "onError", errCode, errInfo);
            }

            @Override
            public void onSuccess() {
                //推流过程中，可能会重复收到PUSH_EVT_PUSH_BEGIN事件，onSuccess可能会被回调多次，如果已经创建的房间，直接返回
                if (mCurrRoomID != null && mCurrRoomID.length() > 0) {
                    return;
                }

                if (mTXLivePusher != null) {
                    TXLivePushConfig config = mTXLivePusher.getConfig();
                    config.setVideoEncodeGop(2);
                    mTXLivePusher.setConfig(config);
                }

                mBackground = false;
            }
        });

    }

    @Override
    public void enterRoom(String roomID, TXCloudVideoView view, IMLVBLiveRoomListener.EnterRoomCallback callback) {

    }

    @Override
    public void exitRoom(IMLVBLiveRoomListener.ExitRoomCallback callback) {

    }

    @Override
    public void setCustomInfo(MLVBCommonDef.CustomFieldOp op, String key, Object value, IMLVBLiveRoomListener.SetCustomInfoCallback callback) {

    }

    @Override
    public void getCustomInfo(IMLVBLiveRoomListener.GetCustomInfoCallback callback) {

    }

    private void destroy() {
        mHeartBeatThread.stopHeartbeat();
    }


    @Override
    public void requestJoinAnchor(String reason, IMLVBLiveRoomListener.RequestJoinAnchorCallback callback) {

    }

    @Override
    public int responseJoinAnchor(String userID, boolean agree, String reason) {
        return 0;
    }

    @Override
    public void joinAnchor(IMLVBLiveRoomListener.JoinAnchorCallback callback) {

    }

    @Override
    public void quitJoinAnchor(IMLVBLiveRoomListener.QuitAnchorCallback callback) {

    }

    @Override
    public void kickoutJoinAnchor(String userID) {

    }

    /**
     * 请求跨房PK
     * <p>
     * 主播和主播之间可以跨房间 PK，两个正在直播中的主播 A 和 B，他们之间的跨房 PK 流程如下：
     * 1. 【主播 A】调用 requestRoomPK() 向主播 B 发起连麦请求。
     * 2. 【主播 B】会收到 {@link IMLVBLiveRoomListener#onRequestRoomPK(AnchorInfo)} 回调通知。
     * 3. 【主播 B】调用 responseRoomPK() 确定是否接受主播 A 的 PK 请求。
     * 4. 【主播 B】如果接受了主播 A 的要求，可以直接调用 startRemoteView() 来显示主播 A 的视频画面。
     * 5. 【主播 A】会收到 {@link IMLVBLiveRoomListener.RequestRoomPKCallback} 回调通知，可以得知请求是否被同意。
     * 6. 【主播 A】如果请求被同意，则可以调用 startRemoteView() 显示主播 B 的视频画面。
     *
     * @param userID   被邀约主播ID
     * @param callback 请求跨房PK的结果回调
     * @see {@link IMLVBLiveRoomListener#onRequestRoomPK(AnchorInfo)}
     */
    @Override
    public void requestRoomPK(String userID, IMLVBLiveRoomListener.RequestRoomPKCallback callback) {
        TXCLog.i(TAG, "API -> requestRoomPK:" + userID);
        try {
            CommonJson<HeartBeatThread.PKRequest> request = new CommonJson<>();
            request.cmd = "pk";
            request.data = new HeartBeatThread.PKRequest();
            request.data.type = "request";
            request.data.action = "start";
            request.data.roomID = mCurrRoomID;
            request.data.userID = mSelfAccountInfo.userID;
            request.data.userName = mSelfAccountInfo.userName;
            request.data.userAvatar = mSelfAccountInfo.userAvatar;
            request.data.accelerateURL = mSelfAccelerateURL;
            request.data.timestamp = System.currentTimeMillis() - mTimeDiff;

            final IMLVBLiveRoomListener.RequestRoomPKCallback[] mRequestPKCallback = {callback};

            if (mRequestPKTimeoutTask == null) {
                mRequestPKTimeoutTask = new Runnable() {
                    @Override
                    public void run() {
                        callbackOnThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mRequestPKCallback[0] != null) {
                                    mRequestPKCallback[0].onTimeOut();
                                    mRequestPKCallback[0] = null;
                                }
                            }
                        });
                    }

                    private void callbackOnThread(Runnable runnable) {
                    }
                };
            }

            mListenerHandler.removeCallbacks(mRequestPKTimeoutTask);
            //10秒收不到主播同意/拒绝 PK 的响应，则回调超时
            mListenerHandler.postDelayed(mRequestPKTimeoutTask, 10 * 1000);

            mPKAnchorInfo = new AnchorInfo(userID, "", "", "");

//            String content = new Gson().toJson(request, new TypeToken<CommonJson<PKRequest>>() {
//            }.getType());
//            IMMessageMgr imMessageMgr = mIMMessageMgr;
//            if (imMessageMgr != null) {
//                imMessageMgr.sendC2CCustomMessage(userID, content, new IMMessageMgr.Callback() {
//                    @Override
//                    public void onError(final int code, final String errInfo) {
//                        callbackOnThread(callback, "onError", code, mAppContext.getString(R.string.mlvb_im_request_fail, errInfo, code));
//                    }
//
//                    @Override
//                    public void onSuccess(Object... args) {
//
//                    }
//                });
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int responseRoomPK(String userID, boolean agree, String reason) {
        Log.i(TAG, "API -> responseRoomPK:" + userID + ":" + agree + ":" + reason);
        if (mPlayers.size() > 0 && mMixMode == STREAM_MIX_MODE_JOIN_ANCHOR) {
//            Log.e(TAG, mAppContext.getString(R.string.mlvb_online_conn_exit_then_pk));
            return -1;
        }
        try {
            if (agree) {
                mMixMode = STREAM_MIX_MODE_PK;
            }
            CommonJson<PKResponse> response = new CommonJson<>();
            response.cmd = "pk";
            response.data = new PKResponse();
            response.data.type = "response";
            response.data.result = agree ? "accept" : "reject";
            response.data.reason = reason;
            response.data.roomID = mCurrRoomID;
            response.data.accelerateURL = mSelfAccelerateURL;
            response.data.timestamp = System.currentTimeMillis() - mTimeDiff;

//            String content = new Gson().toJson(response, new TypeToken<CommonJson<PKResponse>>() {
//            }.getType());
            return '0';

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void quitRoomPK(IMLVBLiveRoomListener.QuitRoomPKCallback callback) {

    }

    @Override
    public void startLocalPreview(boolean frontCamera, TXCloudVideoView view) {

    }

    @Override
    public void stopLocalPreview() {

    }

    @Override
    public void startRemoteView(AnchorInfo anchorInfo, TXCloudVideoView view, IMLVBLiveRoomListener.PlayCallback callback) {

    }

    @Override
    public void stopRemoteView(AnchorInfo anchorInfo) {

    }

    @Override
    public void startScreenCapture() {

    }

    @Override
    public void stopScreenCapture() {

    }

    @Override
    public void muteLocalAudio(boolean mute) {

    }

    @Override
    public void muteRemoteAudio(String userID, boolean mute) {

    }

    @Override
    public void muteAllRemoteAudio(boolean mute) {

    }

    @Override
    public void switchCamera() {

    }

    @Override
    public boolean setZoom(int distance) {
        return false;
    }

    @Override
    public boolean enableTorch(boolean enable) {
        return false;
    }

    @Override
    public void setCameraMuteImage(Bitmap bitmap) {

    }

    @Override
    public void setCameraMuteImage(int id) {

    }

    @Override
    public TXBeautyManager getBeautyManager() {
        return null;
    }

    @Override
    public boolean setBeautyStyle(int beautyStyle, int beautyLevel, int whitenessLevel, int ruddinessLevel) {
        return false;
    }

    @Override
    public void setFilter(Bitmap image) {

    }

    @Override
    public void setFilterConcentration(float concentration) {

    }

    @Override
    public void setWatermark(Bitmap image, float x, float y, float width) {

    }

    @Override
    public void setMotionTmpl(String filePaht) {

    }

    @Override
    public boolean setGreenScreenFile(String file) {
        return false;
    }

    @Override
    public void setEyeScaleLevel(int level) {

    }

    @Override
    public void setFaceVLevel(int level) {

    }

    @Override
    public void setFaceSlimLevel(int level) {

    }

    @Override
    public void setFaceShortLevel(int level) {

    }

    @Override
    public void setChinLevel(int chinLevel) {

    }

    @Override
    public void setNoseSlimLevel(int noseSlimLevel) {

    }

    @Override
    public void setExposureCompensation(float value) {

    }

    @Override
    public void sendRoomTextMsg(String message, IMLVBLiveRoomListener.SendRoomTextMsgCallback callback) {

    }

    @Override
    public void sendRoomCustomMsg(String cmd, String message, IMLVBLiveRoomListener.SendRoomCustomMsgCallback callback) {

    }

    @Override
    public boolean playBGM(String path) {
        return false;
    }

    @Override
    public void setBGMNofify(TXLivePusher.OnBGMNotify notify) {

    }

    @Override
    public void stopBGM() {

    }

    @Override
    public void pauseBGM() {

    }

    @Override
    public void resumeBGM() {

    }

    @Override
    public int getBGMDuration(String path) {
        return 0;
    }

    @Override
    public void setMicVolumeOnMixing(int volume) {

    }

    @Override
    public void setBGMVolume(int volume) {

    }

    @Override
    public void setReverbType(int reverbType) {

    }

    @Override
    public void setVoiceChangerType(int voiceChangerType) {

    }

    @Override
    public void setBGMPitch(float pitch) {

    }

    @Override
    public boolean setBGMPosition(int position) {
        return false;
    }

    @Override
    public TXAudioEffectManager getAudioEffectManager() {
        return null;
    }

    private  class PlayerItem {
        public TXCloudVideoView view;
        public AnchorInfo       anchorInfo;
        public TXLivePlayer player;

        public PlayerItem(TXCloudVideoView view, AnchorInfo anchorInfo, TXLivePlayer player) {
            this.view = view;
            this.anchorInfo = anchorInfo;
            this.player = player;
        }

        public void resume(){
            this.player.resume();
        }

        public void pause(){
            this.player.pause();
        }

        public void destroy(){
            this.player.stopPlay(true);
            this.view.onDestroy();
        }
    }

    private class PKResponse {
        public String type;
        public String roomID;
        public String result;
        public String reason;
        public String accelerateURL;
        public long   timestamp;
    }

    protected class CommonJson<T> {
        public String cmd;
        public T      data;
        public CommonJson() {
        }
    }

    private class StreamMixturer {
        private String mMainStreamId = "";
        private String mPKStreamId = "";
        private Vector<String> mSubStreamIds = new java.util.Vector<String>();
        private int mMainStreamWidth = 540;
        private int mMainStreamHeight = 960;

        public StreamMixturer() {

        }

        public void setMainVideoStream(String streamUrl) {
            mMainStreamId = getStreamIDByStreamUrl(streamUrl);

            Log.e(TAG, "MergeVideoStream: setMainVideoStream " + mMainStreamId);
        }

        public void setMainVideoStreamResolution(int width, int height) {
            if (width > 0 && height > 0) {
                mMainStreamWidth = width;
                mMainStreamHeight = height;
            }
        }

        public void addSubVideoStream(String streamUrl) {
            if (mSubStreamIds.size() > 3) {
                return;
            }

            String streamId = getStreamIDByStreamUrl(streamUrl);

            Log.e(TAG, "MergeVideoStream: addSubVideoStream " + streamId);

            if (streamId == null || streamId.length() == 0) {
                return;
            }

            for (String item : mSubStreamIds) {
                if (item.equalsIgnoreCase(streamId)) {
                    return;
                }
            }

            mSubStreamIds.add(streamId);
            sendStreamMergeRequest(5);
        }

        public void delSubVideoStream(String streamUrl) {
            String streamId = getStreamIDByStreamUrl(streamUrl);

            Log.e(TAG, "MergeVideoStream: delSubVideoStream " + streamId);

            boolean bExist = false;
            for (String item : mSubStreamIds) {
                if (item.equalsIgnoreCase(streamId)) {
                    bExist = true;
                    break;
                }
            }

            if (bExist == true) {
                mSubStreamIds.remove(streamId);
                sendStreamMergeRequest(1);
            }
        }

        public void addPKVideoStream(String streamUrl) {
            mPKStreamId = getStreamIDByStreamUrl(streamUrl);
            if (mMainStreamId == null || mMainStreamId.length() == 0 || mPKStreamId == null || mPKStreamId.length() == 0) {
                return;
            }

            Log.e(TAG, "MergeVideoStream: addPKVideoStream " + mPKStreamId);

            final JSONObject requestParam = createPKRequestParam();
            if (requestParam == null) {
                return;
            }

            internalSendRequest(5, true, requestParam);
        }

        public void delPKVideoStream(String streamUrl) {
            mPKStreamId = null;
            if (mMainStreamId == null || mMainStreamId.length() == 0) {
                return;
            }

            String streamId = getStreamIDByStreamUrl(streamUrl);
            Log.e(TAG, "MergeVideoStream: delPKStream");

            final JSONObject requestParam = createPKRequestParam();
            if (requestParam == null) {
                return;
            }

            internalSendRequest(1, true, requestParam);
        }

        public void resetMergeState() {
            Log.e(TAG, "MergeVideoStream: resetMergeState");

            mSubStreamIds.clear();
            mMainStreamId = null;
            mPKStreamId = null;
            mMainStreamWidth = 540;
            mMainStreamHeight = 960;
        }

        private void sendStreamMergeRequest(final int retryCount) {
            if (mMainStreamId == null || mMainStreamId.length() == 0) {
                return;
            }

            final JSONObject requestParam = createRequestParam();
            if (requestParam == null) {
                return;
            }

            internalSendRequest(retryCount, true, requestParam);
        }

        private void internalSendRequest(final int retryIndex, final boolean runImmediately, final JSONObject requestParam) {
            new Thread() {
                @Override
                public void run() {
                    if (runImmediately == false) {
                        try {
                            sleep(2000, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    String streamsInfo = "mainStream: " + mMainStreamId;
                    for (int i = 0; i < mSubStreamIds.size(); ++i) {
                        streamsInfo = streamsInfo + " subStream" + i + ": " + mSubStreamIds.get(i);
                    }

                    Log.e(TAG, "MergeVideoStream: send request, " + streamsInfo + " retryIndex: " + retryIndex + "    " + requestParam.toString());
//                    if (mHttpRequest != null) {
//                        mHttpRequest.mergeStream(mCurrRoomID, mSelfAccountInfo.userID, requestParam, new HttpRequests.OnResponseCallback<HttpResponse.MergeStream>() {
//                            @Override
//                            public void onResponse(int retcode, String strMessage, HttpResponse.MergeStream result) {
//                                Log.e(TAG, "MergeVideoStream: recv response, message = " + (result != null ? "[code = " + result.code + " msg = " + result.message + " merge_code = " + result.merge_code + "]" : "null"));
//
//                                if (result != null && result.code == 0 && result.merge_code == 0) {
//                                    return;
//                                } else {
//                                    int tempRetryIndex = retryIndex - 1;
//                                    if (tempRetryIndex > 0) {
//                                        internalSendRequest(tempRetryIndex, false, requestParam);
//                                    }
//                                }
//                            }
//                        });
//                    }
                }
            }.start();
        }

        private JSONObject createRequestParam() {

            JSONObject requestParam = null;

            try {
                // input_stream_list
                JSONArray inputStreamList = new JSONArray();

                // 大主播
                {
                    JSONObject layoutParam = new JSONObject();
                    layoutParam.put("image_layer", 1);

                    JSONObject mainStream = new JSONObject();
                    mainStream.put("input_stream_id", mMainStreamId);
                    mainStream.put("layout_params", layoutParam);

                    inputStreamList.put(mainStream);
                }

                int subWidth = 160;
                int subHeight = 240;
                int offsetHeight = 90;
                if (mMainStreamWidth < 540 || mMainStreamHeight < 960) {
                    subWidth = 120;
                    subHeight = 180;
                    offsetHeight = 60;
                }
                int subLocationX = mMainStreamWidth - subWidth;
                int subLocationY = mMainStreamHeight - subHeight - offsetHeight;

                // 小主播
                int layerIndex = 0;
                for (String item : mSubStreamIds) {
                    JSONObject layoutParam = new JSONObject();
                    layoutParam.put("image_layer", layerIndex + 2);
                    layoutParam.put("image_width", subWidth);
                    layoutParam.put("image_height", subHeight);
                    layoutParam.put("location_x", subLocationX);
                    layoutParam.put("location_y", subLocationY - layerIndex * subHeight);

                    JSONObject subStream = new JSONObject();
                    subStream.put("input_stream_id", item);
                    subStream.put("layout_params", layoutParam);

                    inputStreamList.put(subStream);
                    ++layerIndex;
                }

                // para
                JSONObject para = new JSONObject();
                para.put("app_id", "");
                para.put("interface", "mix_streamv2.start_mix_stream_advanced");
                para.put("mix_stream_session_id", mMainStreamId);
                para.put("output_stream_id", mMainStreamId);
                para.put("input_stream_list", inputStreamList);

                // interface
                JSONObject interfaceObj = new JSONObject();
                interfaceObj.put("interfaceName", "Mix_StreamV2");
                interfaceObj.put("para", para);

                // requestParam
                requestParam = new JSONObject();
                requestParam.put("timestamp", System.currentTimeMillis() / 1000);
                requestParam.put("eventId", System.currentTimeMillis() / 1000);
                requestParam.put("interface", interfaceObj);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return requestParam;
        }

        private JSONObject createPKRequestParam() {

            if (mMainStreamId == null || mMainStreamId.length() == 0) {
                return null;
            }

            JSONObject requestParam = null;

            try {
                // input_stream_list
                JSONArray inputStreamList = new JSONArray();

                if (mPKStreamId != null && mPKStreamId.length() > 0) {
                    // 画布
                    {
                        JSONObject layoutParam = new JSONObject();
                        layoutParam.put("image_layer", 1);
                        layoutParam.put("input_type", 3);
                        layoutParam.put("image_width", 720);
                        layoutParam.put("image_height", 640);

                        JSONObject canvasStream = new JSONObject();
                        canvasStream.put("input_stream_id", mMainStreamId);
                        canvasStream.put("layout_params", layoutParam);

                        inputStreamList.put(canvasStream);
                    }

                    // mainStream
                    {
                        JSONObject layoutParam = new JSONObject();
                        layoutParam.put("image_layer", 2);
                        layoutParam.put("image_width", 360);
                        layoutParam.put("image_height", 640);
                        layoutParam.put("location_x", 0);
                        layoutParam.put("location_y", 0);

                        JSONObject mainStream = new JSONObject();
                        mainStream.put("input_stream_id", mMainStreamId);
                        mainStream.put("layout_params", layoutParam);

                        inputStreamList.put(mainStream);
                    }

                    // subStream
                    {
                        JSONObject layoutParam = new JSONObject();
                        layoutParam.put("image_layer", 3);
                        layoutParam.put("image_width", 360);
                        layoutParam.put("image_height", 640);
                        layoutParam.put("location_x", 360);
                        layoutParam.put("location_y", 0);

                        JSONObject mainStream = new JSONObject();
                        mainStream.put("input_stream_id", mPKStreamId);
                        mainStream.put("layout_params", layoutParam);

                        inputStreamList.put(mainStream);
                    }
                } else {
                    JSONObject layoutParam = new JSONObject();
                    layoutParam.put("image_layer", 1);

                    JSONObject canvasStream = new JSONObject();
                    canvasStream.put("input_stream_id", mMainStreamId);
                    canvasStream.put("layout_params", layoutParam);

                    inputStreamList.put(canvasStream);
                }

                // para
                JSONObject para = new JSONObject();
                para.put("app_id", "");
                para.put("interface", "mix_streamv2.start_mix_stream_advanced");
                para.put("mix_stream_session_id", mMainStreamId);
                para.put("output_stream_id", mMainStreamId);
                para.put("input_stream_list", inputStreamList);

                // interface
                JSONObject interfaceObj = new JSONObject();
                interfaceObj.put("interfaceName", "Mix_StreamV2");
                interfaceObj.put("para", para);

                // requestParam
                requestParam = new JSONObject();
                requestParam.put("timestamp", System.currentTimeMillis() / 1000);
                requestParam.put("eventId", System.currentTimeMillis() / 1000);
                requestParam.put("interface", interfaceObj);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return requestParam;
        }

        private String getStreamIDByStreamUrl(String strStreamUrl) {
            if (strStreamUrl == null || strStreamUrl.length() == 0) {
                return null;
            }

            //推流地址格式：rtmp://8888.livepush.myqcloud.com/path/8888_test_12345_test?txSecret=aaaa&txTime=bbbb
            //拉流地址格式：rtmp://8888.liveplay.myqcloud.com/path/8888_test_12345_test
            //            http://8888.liveplay.myqcloud.com/path/8888_test_12345_test.flv
            //            http://8888.liveplay.myqcloud.com/path/8888_test_12345_test.m3u8


            String subString = strStreamUrl;

            {
                //1 截取第一个 ？之前的子串
                int index = subString.indexOf("?");
                if (index != -1) {
                    subString = subString.substring(0, index);
                }
                if (subString == null || subString.length() == 0) {
                    return null;
                }
            }

            {
                //2 截取最后一个 / 之后的子串
                int index = subString.lastIndexOf("/");
                if (index != -1) {
                    subString = subString.substring(index + 1);
                }

                if (subString == null || subString.length() == 0) {
                    return null;
                }
            }

            {
                //3 截取第一个 . 之前的子串
                int index = subString.indexOf(".");
                if (index != -1) {
                    subString = subString.substring(0, index);
                }
                if (subString == null || subString.length() == 0) {
                    return null;
                }
            }

            return subString;
        }
    }


    private class TXLivePushListenerImpl implements ITXLivePushListener {
        private StandardCallback mCallback = null;

        public void setCallback(StandardCallback callback) {
            mCallback = callback;
        }

        @Override
        public void onPushEvent(final int event, final Bundle param) {
            if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN) {
                TXCLog.d(TAG, mAppContext.getString(Integer.parseInt("推流成功")));
                callbackOnThread(mCallback, "onSuccess");
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL) {
                String msg = mAppContext.getString(Integer.parseInt("推流失败"));
                TXCLog.e(TAG, msg);
                callbackOnThread(mCallback, "onError", event, msg);
            } else if (event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL) {
                String msg = mAppContext.getString(Integer.parseInt("推流失败"));
                TXCLog.e(TAG, msg);
                callbackOnThread(mCallback, "onError", event, msg);
            } else if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || event == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
                String msg = mAppContext.getString(Integer.parseInt("推流失败"));
                TXCLog.e(TAG, msg);
                callbackOnThread(mCallback, "onError", event, msg);
            } else if (event == TXLiveConstants.PUSH_ERR_SCREEN_CAPTURE_START_FAILED) {
                String msg = mAppContext.getString(Integer.parseInt("推流失败"));
                TXCLog.e(TAG, msg);
                callbackOnThread(mCallback, "onError", event, msg);
            }
        }

        @Override
        public void onNetStatus(Bundle status) {

        }
    }

    protected static class HeartBeatThread {
        private Handler handler;

        public HeartBeatThread() {
        }

        private Runnable heartBeatRunnable = new Runnable() {
            @Override
            public void run() {
                Handler localHandler = handler;
                if (localHandler == null) {
                    return;
                }
//                if (mSelfAccountInfo != null && mSelfAccountInfo.userID != null && mSelfAccountInfo.userID.length() > 0 && mCurrRoomID != null && mCurrRoomID.length() > 0) {
//                    if (mHttpRequest != null) {
//                        mHttpRequest.heartBeat(mSelfAccountInfo.userID, mCurrRoomID, mRoomStatusCode);
//                    }
//                    localHandler.postDelayed(heartBeatRunnable, 5000);
//                }
            }
        };

        public void startHeartbeat() {
            synchronized (this) {
                if (handler != null && handler.getLooper() != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        handler.getLooper().quitSafely();
                    } else {
                        handler.getLooper().quit();
                    }
                }
                HandlerThread thread = new HandlerThread("HeartBeatThread");
                thread.start();
                handler = new Handler(thread.getLooper());
                handler.postDelayed(heartBeatRunnable, 1000);
            }
        }

        public void stopHeartbeat() {
            synchronized (this) {
                if (handler != null) {
                    handler.removeCallbacks(heartBeatRunnable);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        handler.getLooper().quitSafely();
                    } else {
                        handler.getLooper().quit();
                    }
                    handler = null;
                }
            }
        }

        private static class PKRequest {
            public String type;
            public String action;
            public String roomID;
            public String userID;
            public String userName;
            public String userAvatar;
            public String accelerateURL;
            public long   timestamp;
        }
    }

    public interface StandardCallback {
        /**
         * @param errCode 错误码
         * @param errInfo 错误信息
         */
        void onError(int errCode, String errInfo);

        void onSuccess();
    }

    protected void startPushStream(final String url, final int videoQuality, final StandardCallback callback) {
        //在主线程开启推流
        Handler handler = new Handler(mAppContext.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mTXLivePusher != null && mTXLivePushListener != null) {
                    mTXLivePushListener.setCallback(callback);
                    mTXLivePusher.setVideoQuality(videoQuality, false, false);
                    int ret = mTXLivePusher.startPusher(url);
                    if (ret == -5) {
                        String msg = mAppContext.getString(Integer.parseInt("推流失败[license 校验失败"));
                        TXCLog.e(TAG, msg);
                        if (callback != null)
                            callback.onError(MLVBCommonDef.LiveRoomErrorCode.ERROR_LICENSE_INVALID, msg);
                    }
                } else {
                    String msg = mAppContext.getString(Integer.parseInt("推流失败[TXLivePusher未初始化，请确保已经调用startLocalPreview"));
                    TXCLog.e(TAG, msg);
                    if (callback != null) callback.onError(MLVBCommonDef.LiveRoomErrorCode.ERROR_PUSH, msg);
                }
            }
        });
    }


    private void callbackOnThread(final Object object, final String methodName, final Object... args) {
        if (object == null || methodName == null || methodName.length() == 0) {
            return;
        }
        mListenerHandler.post(new Runnable() {
            @Override
            public void run() {
                Class objClass = object.getClass();
                while (objClass != null) {
                    Method[] methods = objClass.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.getName() == methodName) {
                            try {
                                method.invoke(object, args);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                    }
                    objClass = objClass.getSuperclass();
                }
            }
        });
    }

    private void callbackOnThread(final Runnable runnable) {
        if (runnable == null) {
            return;
        }
        mListenerHandler.post(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

}