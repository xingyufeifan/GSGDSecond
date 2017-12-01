package com.nandi.gsgdsecond.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.MonitorUpInfo;
import com.nandi.gsgdsecond.utils.Constant;

public class MonitiorYesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitior_yes);
        MonitorUpInfo monitorUpInfo= (MonitorUpInfo) getIntent().getSerializableExtra(Constant.MONITOR_UP);
        Log.d("cp",monitorUpInfo.toString());
    }
}
