package com.nandi.gsgdsecond.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.adapter.ExpandableAdapter;
import com.nandi.gsgdsecond.bean.DisasterPoint;
import com.nandi.gsgdsecond.bean.MonitorPoint;
import com.nandi.gsgdsecond.greendao.GreenDaoHelper;
import com.nandi.gsgdsecond.service.LocationService;
import com.nandi.gsgdsecond.utils.Constant;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 群测群防主页
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.rl_list)
    RelativeLayout rlList;
    @BindView(R.id.el_disaster_list)
    ExpandableListView elDisasterList;
    @BindView(R.id.btn_videoRecord1)
    Button btn_videoRecord1;
    @BindView(R.id.rl_dailylog)
    RelativeLayout rl_dailylog;
    @BindView(R.id.rl_weekly)
    RelativeLayout rl_weekly;

    private List<DisasterPoint> disasterPoints;
    private Map<String, List<MonitorPoint>> map;
    private ExpandableAdapter adapter;
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
        Intent intent = new Intent(context, LocationService.class);
        intent.putExtra(Constant.UPLOAD_URL, "uploadMeteorLongitudeAndLatitude.do");
        startService(intent);
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

                    if (Patterns.WEB_URL.matcher(result).matches() || URLUtil.isValidUrl(result)) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        try {
                            if (0 == (result.indexOf("http"))) {
                                intent.setData(Uri.parse(result));
                            } else {
                                intent.setData(Uri.parse("https://" + result));
                            }
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    }

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        //应急调查视频录制
        btn_videoRecord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecordVideoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        titleText.setText("灾害点列表");
        rl_dailylog.setVisibility(View.GONE);
        rl_weekly.setVisibility(View.GONE);
        rlList.setVisibility(View.VISIBLE);

        elDisasterList.setGroupIndicator(null);//消除expandlistview的图标
        map = new HashMap<>();
        disasterPoints = GreenDaoHelper.queryDisasterList();//查询保存的灾害点列表
        List<MonitorPoint> queryMonitorList = GreenDaoHelper.queryMonitorList();//查询保存的监测点列表
        if (queryMonitorList != null && queryMonitorList.size() > 0) {//有监测点
            for (DisasterPoint disasterPoint : disasterPoints) {
                String number = disasterPoint.getNumber();
                List<MonitorPoint> monitorPoints = new ArrayList<>();
                monitorPoints.add(new MonitorPoint(null, "宏观观测", null, null, null, null, null));
                for (MonitorPoint monitorPoint : queryMonitorList) {
                    if (monitorPoint.getDisasterNumber().equals(number)) {
                        monitorPoints.add(monitorPoint);
                    }
                }
                map.put(number, monitorPoints);
            }
        } else {//无监测点
            for (DisasterPoint disasterPoint : disasterPoints) {
                String number = disasterPoint.getNumber();
                List<MonitorPoint> monitorPoints = new ArrayList<>();
                monitorPoints.add(new MonitorPoint(null, "宏观观测", null, null, null, null, null));
                map.put(number, monitorPoints);
            }
        }
        adapter = new ExpandableAdapter(disasterPoints, map, this);
        elDisasterList.setAdapter(adapter);
    }

}
