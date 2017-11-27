package com.nandi.gsgdsecond.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.DailyBean;
import com.nandi.gsgdsecond.utils.Api;
import com.nandi.gsgdsecond.utils.CommonUtils;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.MyProgressBar;
import com.nandi.gsgdsecond.utils.SharedUtils;
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
 * 片区专管员：工作周报填报页面
 * Created by baohongyan on 2017/11/22.
 */

public class WeeklyReportActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.et_townsName)
    EditText etTownsName;
    @BindView(R.id.tv_weeklyTime)
    TextView tvWeeklyTime;
    @BindView(R.id.et_userName)
    EditText etUserName;
    @BindView(R.id.et_weeklyWork)
    EditText etWeeklyWork;
    @BindView(R.id.btn_report)
    Button btnReport;

    private WeeklyReportActivity context;
    private ProgressDialog progressDialog;
    private MyProgressBar progressBar;
    private DailyBean listBean;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);
        ButterKnife.bind(this);
        context = this;
        initView();
        initDatas();
        listBean = (DailyBean) getIntent().getSerializableExtra(Constant.DISASTER);
        type = getIntent().getIntExtra("type", 0);
        if (3 == type) {
            initData();
        }
    }

    private void initData() {
        etTownsName.setText(listBean.getUnits());
        tvWeeklyTime.setText(listBean.getRecord_time());
        etUserName.setText(listBean.getUser_name());
        etWeeklyWork.setText(listBean.getJobContent());
        btnReport.setVisibility(View.GONE);
    }

    private void initView() {
        tv_title.setText("工作周报");
        tvWeeklyTime.setText(CommonUtils.getSystemTime());
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在上传...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressBar = new MyProgressBar(context);
    }

    /**
     * 初始化默认数据：乡镇名称、片区负责人名称
     */
    private void initDatas() {
        etTownsName.setText((String) SharedUtils.getShare(context, Constant.TOWNS, ""));
        etUserName.setText((String) SharedUtils.getShare(context, Constant.WEEKLYNAME, ""));
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @OnClick({R.id.iv_back, R.id.iv_call, R.id.btn_report})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                back();
                break;
            case R.id.iv_call:
                getNumber();
                break;
            case R.id.btn_report:
                if (checkEditText()) {
                    ToastUtils.showShort(context, "请输入完整信息");
                } else {
                    SharedUtils.putShare(context, Constant.TOWNS, etTownsName.getText().toString().trim());
                    SharedUtils.putShare(context, Constant.WEEKLYNAME, etUserName.getText().toString().trim());
                    try {
                        reportRequest(new Api(context).getWeeklyUrl());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    /**
     * 返回上一级
     */
    private void back() {
        if (3 == type){
            finish();
        } else {
            CommonUtils.back(context, "确定要退出周报填写吗？");
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

    /**
     * 判断数据是否为空
     *
     * @return
     */
    private boolean checkEditText() {
        if (etTownsName.getText().toString().trim().length() == 0 ||
                etUserName.getText().toString().trim().length() == 0 ||
                etWeeklyWork.getText().toString().trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 发起上报请求的方法
     *
     * @param url 请求地址
     */
    private void reportRequest(String url) throws JSONException {
        progressDialog.show();
        String phoneNum = (String) SharedUtils.getShare(context, Constant.MOBILE, "");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("member", "");
        jsonObject.put("phoneNum", phoneNum);
        jsonObject.put("units", etTownsName.getText().toString().trim());
        jsonObject.put("recordTime", tvWeeklyTime);
        jsonObject.put("userName", etUserName.getText().toString().trim());
        jsonObject.put("jobContent", etWeeklyWork.getText().toString().trim());
        String data = String.valueOf(jsonObject);
        OkHttpUtils.post().url(url)
                .addParams("data", data)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(context, "网络连接失败,请稍后重试");
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("WeelkyReport---", response);
                        ToastUtils.showShort(context, "周报上传成功!");
                        progressDialog.dismiss();
                        if (3==type){
                            setResult(WeeklyListActivity.DANGER_REQUEST_CODE);
                            finish();
                        }else{
                            context.finish();
                        }
                    }
                });
    }

}
