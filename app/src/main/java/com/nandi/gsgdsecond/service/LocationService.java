package com.nandi.gsgdsecond.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.activity.BaseActivity;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.SharedUtils;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by ChenPeng on 2017/11/25.
 */

public class LocationService extends Service {
    private Context context;
    private String uploadUrl;
    private String mobile;
    private LocationClient locationClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mobile = (String) SharedUtils.getShare(this, Constant.MOBILE, "");
        initLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uploadUrl = intent.getStringExtra(Constant.UPLOAD_URL);
        return START_STICKY;

    }

    public void initLocation() {
         locationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("wgs84");
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(60 * 1000);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setOpenGps(true);
        option.setIgnoreKillProcess(false);
        //可选，默认false,设置是否使用gps
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new MyLocationListener());
        locationClient.start();
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int locType = bdLocation.getLocType();
            if (locType == BDLocation.TypeOffLineLocation || locType == BDLocation.TypeGpsLocation || locType == BDLocation.TypeNetWorkLocation) {
                double lon = bdLocation.getLongitude();
                double lat = bdLocation.getLatitude();
                setPost(lon, lat);
            }
        }

        @Override
        public void onLocDiagnosticMessage(int i, int i1, String s) {
            if (i1 == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_GPS) {
                ToastUtils.showShort(context, "请打开GPS");
            } else if (i1 == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_WIFI) {
                ToastUtils.showShort(context, "建议打开WIFI提高定位经度");
            }
        }

        private void setPost(double lon, double lat) {

            OkHttpUtils.get().url(getResources().getString(R.string.base_url) + uploadUrl)
                    .addParams("phone", mobile)
                    .addParams("lon", String.valueOf(lon))
                    .addParams("lat", String.valueOf(lat))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.d("cp", "上传失败");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d("cp", response);
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationClient.stop();
    }
}
