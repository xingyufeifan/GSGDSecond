package com.nandi.gsgdsecond.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blankj.utilcode.util.ServiceUtils;
import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.adapter.ExpandableAdapter;
import com.nandi.gsgdsecond.bean.DisasterPoint;
import com.nandi.gsgdsecond.bean.MonitorPoint;
import com.nandi.gsgdsecond.greendao.GreenDaoHelper;
import com.nandi.gsgdsecond.service.LocationService;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.DownloadUtils;
import com.nandi.gsgdsecond.utils.MyProgressBar;
import com.nandi.gsgdsecond.utils.SharedUtils;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

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
        Intent intent=new Intent(context, LocationService.class);
        intent.putExtra(Constant.UPLOAD_URL,"uploadMeteorLongitudeAndLatitude.do");
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
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        elDisasterList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Intent intent;
                if (i1 == 0) {//宏观观测监听
                    intent = new Intent(context, DisasterListActivity.class);
                    DisasterPoint disasterPoint = disasterPoints.get(i);
                    intent.putExtra(Constant.DISASTER, disasterPoint);
                } else {//定量监测监听
                    MonitorPoint monitorPoint = map.get(disasterPoints.get(i).getNumber()).get(i1);
                    intent = new Intent(context, MonitorActivity.class);
                    intent.putExtra(Constant.MONITOR, monitorPoint);
                    intent.putExtra(Constant.DISASTER_NAME, disasterPoints.get(i).getName());
                }
                startActivity(intent);
                return true;
            }
        });
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
