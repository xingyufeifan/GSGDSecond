package com.nandi.gsgdsecond.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 驻守人员主页
 * Created by baohongyan on 2017/11/20.
 */

public class DailyLogActivity extends BaseActivity{

    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.rl_list)
    RelativeLayout rlList;
    @BindView(R.id.el_disaster_list)
    ExpandableListView elDisasterList;
    @BindView(R.id.btn_videoRecord2)
    Button btn_videoRecord2;
    @BindView(R.id.rl_dailylog)
    RelativeLayout rl_dailylog;
    @BindView(R.id.rl_weekly)
    RelativeLayout rl_weekly;
    @BindView(R.id.dailylog_report)
    CardView dailyLog_report;
    @BindView(R.id.dailylog_situation)
    CardView dailyLog_situation;
    @BindView(R.id.disaster_report)
    CardView disaster_report;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        initData();
        setListener();
    }

    @Override
    public void initData() {
        super.initData();
        titleText.setText("驻守人员");
        rlList.setVisibility(View.GONE);
        rl_weekly.setVisibility(View.GONE);
        rl_dailylog.setVisibility(View.VISIBLE);
    }

    @Override
    public void setListener() {
        super.setListener();

        //日志填报
        dailyLog_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DailyReportActivity.class));
            }
        });

        //灾情速报
        disaster_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DisReportActivity.class));
            }
        });

        //日志上报情况
        dailyLog_situation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
//                startActivity(new Intent(context, DailyReportActivity.class));
                ToastUtils.showShort(context, "暂时无法查看!");
            }
        });

        //应急调查视频录制
        btn_videoRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
