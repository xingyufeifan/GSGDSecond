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
import android.widget.Toast;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.service.LocationService;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.uuzuche.lib_zxing.activity.CodeUtils;

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
        startLocationService();
    }
    private void startLocationService() {
        Intent intent=new Intent(context, LocationService.class);
        intent.putExtra(Constant.UPLOAD_URL,"receiveLonLat.do");
        startService(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == 101) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(context, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
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
            startActivity(new Intent(context, DailyListActivity.class));
            }
        });

        //应急调查视频录制
        btn_videoRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,RecordVideoActivity.class);
                startActivity(intent);
            }
        });
    }

}
