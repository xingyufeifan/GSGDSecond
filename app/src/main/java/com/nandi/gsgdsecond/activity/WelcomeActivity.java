package com.nandi.gsgdsecond.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.SharedUtils;

public class WelcomeActivity extends AppCompatActivity {
    private boolean isLogin;
    private String imei;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        isLogin= (boolean) SharedUtils.getShare(this, Constant.IS_LOGIN,false);
        imei = (String) SharedUtils.getShare(this, Constant.IMEI, "");
        handler=new Handler();
        checkIsLogin();
    }

    private void checkIsLogin() {
        if (isLogin && imei.equals("0")){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    finish();
                }
            },2000);
        } else if (isLogin && imei.equals("1")) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(WelcomeActivity.this,DailyLogActivity.class));
                    finish();
                }
            },2000);
        } else if (isLogin && imei.equals("2")) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(WelcomeActivity.this,WeeklyActivity.class));
                    finish();
                }
            },2000);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                    finish();
                }
            },2000);
        }
    }
}
