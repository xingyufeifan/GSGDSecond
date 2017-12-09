package com.nandi.gsgdsecond.activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.Message;
import com.nandi.gsgdsecond.service.MyService;

/**
 * 应急视频接听界面
 */
public class ReceiveVideoActivity extends Activity implements OnClickListener {
    private Button btnAccept;
    private Button btnReject;
    private TextView tvMsg;
    private Message message;
    private MediaPlayer player;
    private NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        message = (Message) getIntent().getSerializableExtra("MESSAGE");
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        wakeUpAndUnlock();
        play();
        initView();
    }

    private void play() {
        player = MediaPlayer.create(this, R.raw.alarm);
        player.start();
        player.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                player.start();
                player.setLooping(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
    }

    private void initView() {
        btnAccept = (Button) findViewById(R.id.btn_accept);
        btnReject = (Button) findViewById(R.id.btn_reject);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        btnAccept.setOnClickListener(this);
        btnReject.setOnClickListener(this);
        tvMsg.setText(message.getMessage());
    }

    public void wakeUpAndUnlock() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁  
        if (km.inKeyguardRestrictedInputMode()) {
            kl.disableKeyguard();
        }
        //获取电源管理器对象  
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag  
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕  
        wl.acquire();
        //释放  
        wl.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:
                manager.cancel(1);
                Intent progressIntent = new Intent(this, ProgressActivity.class);
                startActivity(progressIntent);
                Intent i = new Intent(this, MyService.class);
                i.putExtra("ROOM_ID", message.getRoomId());
                i.putExtra("USER_ID", message.getUserId());
                Log.e("视频接听", "进入通话界面。。。。。。。");
                startService(i);
                finish();
                break;
            case R.id.btn_reject:
                finish();
                break;
        }
    }

}
