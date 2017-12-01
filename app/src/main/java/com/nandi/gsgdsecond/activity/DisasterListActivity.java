package com.nandi.gsgdsecond.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nandi.gsgdsecond.R;

public class DisasterListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster);
        String disNum = getIntent().getStringExtra("disNum");
        System.out.println("savedInstanceState = " + disNum);
    }
}
