package com.nandi.gsgdsecond.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.adapter.MonitorUpAdapter;
import com.nandi.gsgdsecond.bean.DisasterUpInfo;
import com.nandi.gsgdsecond.bean.MonitorUpInfo;
import com.nandi.gsgdsecond.utils.CommonUtils;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.SharedUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 定量监测已上报列表页面
 */
public class MonitorListActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.rv_disaster)
    RecyclerView rvMonitor;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.btn_search)
    Button btnSearch;
    private String disNum;
    private String monNum;
    private String mobile;
    private Context context;
    private ProgressDialog progress;
    private MonitorUpAdapter adapter;
    private List<MonitorUpInfo> monitorUpInfos=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster);
        ButterKnife.bind(this);
        context = this;
        initData();
        setListener();
    }

    private void setListener() {
        adapter.setOnItemClickListener(new MonitorUpAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = rvMonitor.getChildAdapterPosition(view);
                Intent intent = new Intent(context, MonitiorYesActivity.class);
                intent.putExtra(Constant.MONITOR_UP, monitorUpInfos.get(position));
                startActivity(intent);
            }
        });
    }

    private void initData() {
        tvTitle.setText("定量监测记录");
        disNum = getIntent().getStringExtra(Constant.DISASTER_NUMBER);
        monNum = getIntent().getStringExtra(Constant.MONITOR_NUMBER);
        mobile = (String) SharedUtils.getShare(context, Constant.MOBILE, "");
        progress = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        progress.setMessage("正在请求数据...");
        progress.setCanceledOnTouchOutside(false);
        adapter=new MonitorUpAdapter(context,monitorUpInfos);
        rvMonitor.setLayoutManager(new LinearLayoutManager(context));
        rvMonitor.setAdapter(adapter);
        String start = getStartTime();
        String end = getTime(new Date());
        setRequest(start, end);
    }
    private String getStartTime() {
        Date date = new Date();//获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);//当前时间减去一年，即一年前的时间
        Date time = calendar.getTime();
        return getTime(time);
    }
    private void getNumber() {
        progress.show();
        OkHttpUtils.get().url(getResources().getString(R.string.base_url) + "getHelpMobile.do")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progress.dismiss();
                        com.nandi.gsgdsecond.utils.ToastUtils.showLong(context, "获取失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progress.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            CommonUtils.callPhone(message, MonitorListActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
    private void setRequest(String startTime, String endTime) {
        progress.show();
        OkHttpUtils.get().url(getResources().getString(R.string.base_url)  + "findHistoryData.do")
                .addParams("monitorOrMacro", "2")
                .addParams("mobile", mobile)
                .addParams("startTime", startTime)
                .addParams("endTime", endTime)
                .addParams("disNo", disNum)
                .addParams("monitorNo", monNum)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progress.dismiss();
                        ToastUtils.showShort("网络连接失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progress.dismiss();
                        Gson gson = new Gson();
                        monitorUpInfos.clear();
                        try {
                            JSONObject object = new JSONObject(response);
                            String data = object.getString("data");
                            List<MonitorUpInfo> infos = gson.fromJson(data, new TypeToken<List<MonitorUpInfo>>() {
                            }.getType());
                            monitorUpInfos.addAll(infos);
                            adapter.notifyDataSetChanged();
                            Log.d("cp",data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    @OnClick({R.id.iv_back, R.id.iv_call, R.id.tv_start_time, R.id.tv_end_time, R.id.btn_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_call:
                getNumber();
                break;
            case R.id.tv_start_time:
                new TimePickerView.Builder(context, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        tvStartTime.setText(getTime(date));
                    }
                })
                        .setSubmitText("确定")
                        .setCancelText("取消")
                        .build()
                        .show();
                break;
            case R.id.tv_end_time:
                new TimePickerView.Builder(context, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        tvEndTime.setText(getTime(date));
                    }
                })
                        .setSubmitText("确定")
                        .setCancelText("取消")
                        .build()
                        .show();
                break;
            case R.id.btn_search:
                String startTime = tvStartTime.getText().toString();
                String endTime = tvEndTime.getText().toString();
                if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
                    ToastUtils.showShort("请先选择时间");
                } else {
                    setRequest(startTime, endTime);
                }
                break;
            default:
        }
    }
}
