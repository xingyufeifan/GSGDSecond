package com.nandi.gsgdsecond.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.DisasterPoint;
import com.nandi.gsgdsecond.bean.MonitorPoint;
import com.nandi.gsgdsecond.greendao.GreenDaoHelper;
import com.nandi.gsgdsecond.utils.Api;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.MyProgressBar;
import com.nandi.gsgdsecond.utils.PermissionUtils;
import com.nandi.gsgdsecond.utils.SharedUtils;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.sevenheaven.segmentcontrol.SegmentControl;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.segment_control)
    SegmentControl mSegments;
    private Context context;
    private MyProgressBar progressBar;
    private String mobile;
    private String imei; //人员类型
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }
        progressBar = new MyProgressBar(context);
        etMobile.setText((String) SharedUtils.getShare(context, Constant.MOBILE, ""));
        imei = (String) SharedUtils.getShare(context, Constant.IMEI, "");
        if (imei.trim().equals("0")||imei.trim().equals("")) {
            imei="0";
            mSegments.setSelectedIndex(0);
        }else if (imei.trim().equals("1")){
            mSegments.setSelectedIndex(1);
        }else if (imei.trim().equals("2")){
            mSegments.setSelectedIndex(2);
        }else if (imei.trim().equals("3")){
            mSegments.setSelectedIndex(3);
        }

        mSegments.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                imei = String.valueOf(index).trim();
                SharedUtils.putShare(context, Constant.IMEI, imei);
            }
        });
    }

    /**
     * 请求所需要的权限
     */
    private void requestPermission() {
        PermissionUtils.requestMultiPermissions(LoginActivity.this, mPermissionGrant);
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_CAMERA:
                    break;
                case PermissionUtils.CODE_RECORD_AUDIO:
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        mobile = etMobile.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort(context, "请先输入电话号码");
            return;
        }
        GreenDaoHelper.deleteAll();//清空灾害点和监测点的数据库信息
        setRequest(new Api(context).getLoginUrl());
    }

    /**
     * 发起请求的方法
     * @param url 请求地址
     */
    private void setRequest(String url) {
        progressBar.show("正在登录");
        OkHttpUtils.get().url(url)
                .addParams("mobile", mobile)     //参数：电话号码
                .addParams("imei", imei.trim())  //参数：请求类型
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(context, "网络连接失败");
                        progressBar.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, response);
                        formatJson(response);
                    }
                });
    }

    /**
     * 解析返回数据
     * @param response
     */
    private void formatJson(String response) {
        try {
            JSONObject object = new JSONObject(response);
            String result = object.getString("result");
            String info = object.getString("info");
            if (imei.trim().equals("0") && count == 0 && "1".equals(result)) {//解析第一次登录请求的信息
                if ("{}".equals(info)) {//如果info里面数据为空说明该号码没有监测点信息
                    ToastUtils.showShort(context, "该号码没有监测点信息");
                    progressBar.dismiss();
                } else {
                    setRequest(new Api(context).getMacoUrl());//登录请求成功，请求灾害点信息
                    count++;
                }
            } else if (imei.trim().equals("0") && count == 1 && "1".equals(result)) {//解析第二次灾害点请求信息
                saveDisaster(info);                       //保存灾害点信息到数据库
                setRequest(new Api(context).getMonitorUrl());//请求监测点信息
                count++;
            } else if (imei.trim().equals("0") && count == 2 && "1".equals(result)) {//解析监测点信息
                if (!"[]".equals(info)) {  //监测点信息不为空
                    Log.d(TAG, "monitor:"+info);
                    saveMonitor(info);
                    progressBar.dismiss();
                    count = 0;
                    SharedUtils.putShare(context,Constant.IS_LOGIN,true);
                    SharedUtils.putShare(context, Constant.MOBILE, mobile);
                    SharedUtils.putShare(context, Constant.IMEI, imei);
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                } else {//监测点信息为空
                    progressBar.dismiss();
                    count = 0;
                    startActivity(new Intent(context, MainActivity.class));
                    SharedUtils.putShare(context,Constant.IS_LOGIN,true);
                    SharedUtils.putShare(context, Constant.MOBILE, mobile);
                    SharedUtils.putShare(context, Constant.IMEI, imei);
                    finish();
                }
            } else if (imei.trim().equals("1")){ //驻守人员
                if ("1".equals(result)){
                    startActivity(new Intent(context, DailyLogActivity.class));
                    SharedUtils.putShare(context,Constant.IS_LOGIN,true);
                    SharedUtils.putShare(context, Constant.MOBILE, mobile);
                    SharedUtils.putShare(context, Constant.IMEI, imei);
                    SharedUtils.putShare(context, Constant.LOGNAME, "");
                    SharedUtils.putShare(context, Constant.WORKTYPE, "");
                    SharedUtils.putShare(context, Constant.SITUATION, "");
                    finish();
                } else {
                    ToastUtils.showShort(context, info);
                    progressBar.dismiss();
                }
            } else if (imei.trim().equals("2")){ //片区专管员
                if ("1".equals(result)){
                    startActivity(new Intent(context, WeeklyActivity.class));
                    SharedUtils.putShare(context,Constant.IS_LOGIN,true);
                    SharedUtils.putShare(context, Constant.MOBILE, mobile);
                    SharedUtils.putShare(context, Constant.IMEI, imei);
                    SharedUtils.putShare(context, Constant.TOWNS, "");
                    SharedUtils.putShare(context, Constant.WEEKLYNAME, "");
                    finish();
                } else {
                    ToastUtils.showShort(context, info);
                    progressBar.dismiss();
                }
            } else if (imei.trim().equals("3")){ //地环站
                if ("1".equals(result)){
                    startActivity(new Intent(context, WeeklyActivity.class));
                    SharedUtils.putShare(context,Constant.IS_LOGIN,true);
                    SharedUtils.putShare(context, Constant.MOBILE, mobile);
                    SharedUtils.putShare(context, Constant.IMEI, imei);
                    finish();
                } else {
                    ToastUtils.showShort(context, info);
                    progressBar.dismiss();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存监测点信息到数据库
     * @param info
     */
    private void  saveMonitor(String info) {
        try {
            JSONArray array = new JSONArray(info);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String unifiedNumber = object.getString("unifiedNumber");
                String monPointNumber = object.getString("monPointNumber");
                MonitorPoint monitorPoint = new MonitorPoint();
                monitorPoint.setName(object.getString("monPointName"));
                monitorPoint.setDisasterNumber(unifiedNumber);
                monitorPoint.setMonitorNumber(monPointNumber);
                monitorPoint.setType(object.getString("monContent"));
                monitorPoint.setDimension(object.getString("dimension"));
                monitorPoint.setMonitorType(object.getString("monType"));
                MonitorPoint queryMonitor = GreenDaoHelper.queryMonitor(unifiedNumber, monPointNumber);
                if (queryMonitor == null) {
                    GreenDaoHelper.insertMonitor(monitorPoint);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存灾害点信息到数据库
     * @param info
     */
    private void saveDisaster(String info) {
        try {
            JSONArray array = new JSONArray(info);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String unifiedNumber = object.getString("unifiedNumber");
                DisasterPoint disasterPoint = new DisasterPoint();
                disasterPoint.setName(object.getString("name"));
                disasterPoint.setNumber(unifiedNumber);
                disasterPoint.setType(object.getString("disasterType"));
                disasterPoint.setLongitude(object.getString("longitude"));
                disasterPoint.setLatitude(object.getString("latitude"));
                disasterPoint.setDisasterType(object.getString("macroscopicPhenomenon"));
                DisasterPoint queryDisaster = GreenDaoHelper.queryDisaster(unifiedNumber);
                if (queryDisaster == null) {
                    GreenDaoHelper.insertDisaster(disasterPoint);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
