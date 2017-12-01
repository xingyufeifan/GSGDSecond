package com.nandi.gsgdsecond.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonitorListActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.titlebar)
    RelativeLayout titlebar;
    @BindView(R.id.data_show)
    RecyclerView dataShow;
    @BindView(R.id.refresh_show)
    SwipeRefreshLayout refreshShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_list);
        ButterKnife.bind(this);
        String disNum = getIntent().getStringExtra("disNum");
        String monNum = getIntent().getStringExtra("monNum");
        System.out.println("monNum+disNum = " + monNum + "=======" + disNum);
        initView();
    }

    private void initView() {
    }
}
