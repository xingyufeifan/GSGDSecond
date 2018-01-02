package com.nandi.gsgdsecond.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.nandi.gsgdsecond.activity.VideoActivity;
import com.nandi.gsgdsecond.utils.ConfigEntity;
import com.nandi.gsgdsecond.utils.ConfigService;
import com.nandi.gsgdsecond.utils.SharedUtils;
import com.nandi.gsgdsecond.utils.ToastUtils;

/**
 * Created by ChenPeng on 2017/5/31.
 */

public class MyService extends Service implements AnyChatBaseEvent {
    private static String TAG="MyService";
    public AnyChatCoreSDK anyChatSDK;
    private String ip;
    private int port;
    private int roomid;
    private int targetid;
    private boolean isOnline;
    private final int LOCALVIDEOAUTOROTATION = 1; // 本地视频自动旋转控制
    private MyBroadReceiver mBroadcastReceiver;
    private Intent stopIntent=new Intent();
    private String name;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stopIntent.setAction("CLOSE_PROGRESS");
        IntentFilter filter = new IntentFilter();
        filter.addAction("stop_service");
        mBroadcastReceiver = new MyBroadReceiver();
        registerReceiver(mBroadcastReceiver, filter);
        name= (String) SharedUtils.getShare(this,"NAME","自己");
        ip= (String) SharedUtils.getShare(this,"IP","183.230.108.112");
        port= (int) SharedUtils.getShare(this,"PORT",8906);
        Log.e("MyService", "启动了服务.....");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        InitSDK();
        roomid=intent.getIntExtra("ROOM_ID",1);
        targetid=intent.getIntExtra("USER_ID",0);
        anyChatSDK.Connect(ip,port);
        anyChatSDK.Login(name,"123");
        ApplyVideoConfig();
        return START_NOT_STICKY;
    }
    private void ApplyVideoConfig() {
        ConfigEntity configEntity = ConfigService.LoadConfig(this);
        if (configEntity.mConfigMode == 1) // 自定义视频参数配置
        {
            // 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL,
                    configEntity.mVideoBitrate);
//			if (configEntity.mVideoBitrate == 0) {
            // 设置本地视频编码的质量
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL,
                    configEntity.mVideoQuality);
//			}
            // 设置本地视频编码的帧率
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL,
                    configEntity.mVideoFps);
            // 设置本地视频编码的关键帧间隔
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL,
                    configEntity.mVideoFps * 4);
            // 设置本地视频采集分辨率
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL,
                    configEntity.mResolutionWidth);
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL,
                    configEntity.mResolutionHeight);
            // 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL,
                    configEntity.mVideoPreset);
        }
        // 让视频参数生效
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM,
                configEntity.mConfigMode);
        // P2P设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC,
                configEntity.mEnableP2P);
        // 本地视频Overlay模式设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY,
                configEntity.mVideoOverlay);
        // 回音消除设置
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL,
                configEntity.mEnableAEC);
        // 平台硬件编码设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC,
                configEntity.mUseHWCodec);
        // 视频旋转模式设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL,
                configEntity.mVideoRotateMode);
        // 本地视频采集偏色修正设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA,
                configEntity.mFixColorDeviation);
        // 视频GPU渲染设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER,
                configEntity.mVideoShowGPURender);
        // 本地视频自动旋转设置
        AnyChatCoreSDK.SetSDKOptionInt(
                AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
                configEntity.mVideoAutoRotation);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        anyChatSDK.LeaveRoom(-1);
        anyChatSDK.Logout();
        anyChatSDK.Release();
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private void InitSDK() {
        if (anyChatSDK == null) {
            anyChatSDK = AnyChatCoreSDK.getInstance(this);
            anyChatSDK.SetBaseEvent(this);
            anyChatSDK.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
            AnyChatCoreSDK.SetSDKOptionInt(
                    AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
                    LOCALVIDEOAUTOROTATION);
        }
    }
    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
        if(bSuccess){
            Log.d(TAG,"连接服务器成功！");
        }else {
            Log.d(TAG,"连接服务器失败！");
            sendBroadcast(stopIntent);
            ToastUtils.showShort(getApplicationContext(),"网络不通畅，正在重新连接...");
        }
    }

    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
        if (dwErrorCode==0){
            Log.d(TAG,"登录成功！");
            anyChatSDK.EnterRoom(roomid,"");
        }else {
            Log.d(TAG,"登录失败！");
        }
    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        if(dwErrorCode==0){
            Log.d(TAG,"进入房间成功！");
            sendBroadcast(stopIntent);
        }else {
            Log.d(TAG,"进入房间失败！");
            sendBroadcast(stopIntent);
        }
    }

    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        int[] users=anyChatSDK.GetRoomOnlineUsers(dwRoomId);
        for (int user : users) {
            if (user==targetid){
                isOnline=true;
            }
        }
        if (isOnline){
            Intent intent=new Intent();
            intent.putExtra("UserID",targetid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(this,VideoActivity.class);
            startActivity(intent);
        }else {
            ToastUtils.showShort(getApplicationContext(),"回话邀请已过期，或对方不在房间内！");
            stopSelf();
        }
    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {

    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {

    }
    private class MyBroadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("stop_service")) {
                stopSelf();
                Log.e("MyBroadReceiver", "收到stop service广播");
            }
        }
    }
}
