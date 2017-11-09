package com.nandi.gsgdsecond.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.activity.ReceiveVideoActivity;
import com.nandi.gsgdsecond.bean.Message;
import com.nandi.gsgdsecond.service.MyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by ChenPeng on 2017/10/19.
 */

public class MyReceiver extends MessageReceiver {
    // 消息接收部分的LOG_TAG
    public static final String REC_TAG = "receiver";
    private Notification notification;
    private NotificationManager manager;
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        // TODO 处理推送通知
        Log.e(REC_TAG, "Receive notification, title: " + title + ", summary: " + summary + ", extraMap: " + extraMap);
    }
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        Log.e("MyMessageReceiver", "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
        String content = cPushMessage.getContent();
        manager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.e(REC_TAG, "onMessage, messageId: " + cPushMessage.getMessageId() + ", title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
        try {
            JSONObject object=new JSONObject(content);
            Message message=new Message();
            message.setUserId(object.getInt("userid"));
            message.setInviteMan(object.getString("invite"));
            message.setMessage(object.getString("message"));
            message.setRoomId(object.getInt("roomid"));
            Intent intent=new Intent(context, ReceiveVideoActivity.class);
            intent.putExtra("MESSAGE",message);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            sendNotification(context,message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void sendNotification(Context context,Message message) {
        Notification.Builder builder=new Notification.Builder(context);
        Intent intent=new Intent();
        intent.setClass(context,MyService.class);
        intent.putExtra("ROOM_ID",message.getRoomId());
        intent.putExtra("USER_ID",message.getUserId());
        PendingIntent pendingIntent=PendingIntent.getService(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification=builder.setContentTitle("视频来电")
                .setContentText(message.getMessage())
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1,notification);
    }
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.e(REC_TAG, "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.e(REC_TAG, "onNotificationClickedWithNoAction, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.e(REC_TAG, "onNotificationReceivedInApp, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }
    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        Log.e(REC_TAG, "onNotificationRemoved");
    }
}
