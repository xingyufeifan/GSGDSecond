package com.nandi.gsgdsecond.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.MonitorUpInfo;
import com.nandi.gsgdsecond.utils.CommonUtils;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.MyProgressBar;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 定量监测已上报数据页面
 */
public class MonitiorYesActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.disName)
    TextView disName;
    @BindView(R.id.monitorName)
    TextView monitorName;
    @BindView(R.id.disTime)
    TextView disTime;
    @BindView(R.id.disValide)
    TextView disValide;
    @BindView(R.id.disState)
    TextView disState;
    @BindView(R.id.warn_level)
    TextView warnLevel;
    @BindView(R.id.monitorLon)
    TextView monitorLon;
    @BindView(R.id.monitorLat)
    TextView monitorLat;
    @BindView(R.id.monitorPhoto)
    ImageView monitorPhoto;
    @BindView(R.id.monitor_data)
    TextView monitorData;
    private MonitiorYesActivity context;
    private MyProgressBar progressBar;
    private MonitorUpInfo monitorUpInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitior_yes);
        ButterKnife.bind(this);
        context = this;
        monitorUpInfo = (MonitorUpInfo) getIntent().getSerializableExtra(Constant.MONITOR_UP);
        initView();
    }

    private void initView() {
        tvTitle.setText("已上报监测数据");
        progressBar = new MyProgressBar(context);
        monitorData.setText( monitorUpInfo.getMonitor_data()+"");
        disName.setText(monitorUpInfo.getDis_name());
        monitorName.setText(monitorUpInfo.getMonitor_name());
        disTime.setText(monitorUpInfo.getU_time());
        int is_validate = monitorUpInfo.getIs_valid();
        if (1 == is_validate) {
            disValide.setText("合法");
        } else {
            disValide.setText("不合法");
        }
        int state = monitorUpInfo.getState();
        if (1 == state) {
            disState.setText("合法");
        } else {
            disState.setText("不合法");
        }
        int warn_level = monitorUpInfo.getWarn_level();
        if (1 == warn_level) {
            warnLevel.setText("蓝色告警");
        } else if (2 == warn_level) {
            warnLevel.setText("黄色告警");
        } else if (3 == warn_level) {
            warnLevel.setText("橙色告警");
        } else if (4 == warn_level) {
            warnLevel.setText("红色告警");
        } else if (-1 == warn_level) {
            warnLevel.setText("异常");
        } else {
            warnLevel.setText("正常");
        }
        monitorLon.setText(monitorUpInfo.getLon() + "");
        monitorLat.setText(monitorUpInfo.getLat() + "");
        if (!TextUtils.isEmpty(monitorUpInfo.getMonitor_url().toString().trim())
                && !"2323232.jpg".equals(monitorUpInfo.getMonitor_url().toString().trim())){
            Glide.with(context).load("http://183.230.108.112/meteor/downImage.do?imageName=" + monitorUpInfo.getMonitor_url()).into(monitorPhoto);
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_call,R.id.monitorPhoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_call:
                getNumber();
                break;
            case  R.id.monitorPhoto:
                View v = LayoutInflater.from(context).inflate(R.layout.dialog_enlarge_photo, null);
                PhotoView photoView = (PhotoView) v.findViewById(R.id.pv_image);
                Glide.with(context).load("http://183.230.108.112/meteor/downImage.do?imageName=" + monitorUpInfo.getMonitor_url())
                        .placeholder(R.drawable.downloading).error(R.drawable.download_pass).into(photoView);
                new AlertDialog.Builder(context, R.style.Transparent)
                        .setView(v)
                        .show();
        }
    }

    private void getNumber() {
        progressBar.show("正在获取号码");
        OkHttpUtils.get().url(getResources().getString(R.string.base_url) + "getHelpMobile.do")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressBar.dismiss();
                        ToastUtils.showLong(context, "获取失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressBar.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            CommonUtils.callPhone(message, context);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
