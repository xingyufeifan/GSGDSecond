package com.nandi.gsgdsecond.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.utils.Api;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.MyProgressBar;
import com.nandi.gsgdsecond.utils.SharedUtils;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 驻守人员：日志填报界面
 * Created by baohongyan on 2017/11/21.
 */

public class DailyReportActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.tv_recordtime)
    TextView recordtime;  //记录时间
    @BindView(R.id.et_recorder)
    EditText etName; //记录人员
    @BindView(R.id.et_worktype)
    EditText etWorkType; //工作类型
    @BindView(R.id.et_situation)
    EditText etSituation; //在岗情况
    @BindView(R.id.et_dailywork)
    EditText etLogContent; //日志内容
    @BindView(R.id.et_remarks)
    EditText etRemarks;  //备注

    private DailyReportActivity context;
    private ProgressDialog progressDialog;
    private MyProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailylog);
        ButterKnife.bind(this);
        context = this;
        initView();
        initDatas();
    }

    private void initView(){
        recordtime.setText(getSystemTime());
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在上传...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressBar = new MyProgressBar(context);
    }

    /**
     * 初始化默认数据：记录人、工作类型、在岗情况
     */
    private void initDatas(){
        etName.setText((String) SharedUtils.getShare(context, Constant.LOGNAME, ""));
        etWorkType.setText((String) SharedUtils.getShare(context, Constant.WORKTYPE, ""));
        etSituation.setText((String) SharedUtils.getShare(context, Constant.SITUATION, ""));
    }

    /**
     * 获取当前时间
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String date = sDateFormat.format(new Date(System.currentTimeMillis()));
        return date;
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @OnClick({R.id.iv_back, R.id.iv_call, R.id.btn_report, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                back();
                break;
            case R.id.iv_call:
                getNumber();
                break;
            case R.id.btn_save:

                break;
            case R.id.btn_report:
                if (checkEditText()){
                    ToastUtils.showShort(context, "请输入完整信息");
                } else {
                    SharedUtils.putShare(context, Constant.LOGNAME, etName.getText().toString().trim());
                    SharedUtils.putShare(context, Constant.WORKTYPE, etWorkType.getText().toString().trim());
                    SharedUtils.putShare(context, Constant.SITUATION, etSituation.getText().toString().trim());
                }
                reportRequest(new Api(context).getLogReportUrl());
                break;
        }
    }

    /**
     * 返回上一级
     */
    private void back() {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setIcon(R.drawable.warning)
                .setMessage("确定要退出日志填写吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

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
                            callPhone(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void callPhone(final String message) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("是否发起电话帮助？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + message));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    /**
     * 判断数据是否为空
     * @return
     */
    private boolean checkEditText(){
        if (recordtime.getText().toString().trim().length()==0 ||
                etName.getText().toString().trim().length()==0 ||
                etWorkType.getText().toString().trim().length()==0 ||
                etSituation.getText().toString().trim().length()==0 ||
                etLogContent.getText().toString().trim().length()==0 ||
                etRemarks.getText().toString().trim().length()==0){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 发起上报请求的方法
     * @param url 请求地址
     */
    private void reportRequest(String url){
        progressDialog.show();
        String phoneNum = (String) SharedUtils.getShare(context, Constant.MOBILE, "");
        String imei = (String) SharedUtils.getShare(context, Constant.IMEI, "");
        OkHttpUtils.post().url(url)
                .addParams("phoneNum", phoneNum)
                .addParams("userName", etName.getText().toString().trim())
                .addParams("recordTime", recordtime.getText().toString().trim())
                .addParams("workType", etWorkType.getText().toString().trim())
                .addParams("situation", etSituation.getText().toString().trim())
                .addParams("logContent", etLogContent.getText().toString().trim())
                .addParams("remarks", etRemarks.getText().toString().trim())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(context, "网络连接失败,请稍后重试");
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("DailyReport---", response);
                        ToastUtils.showShort(context, "日志上传成功!");
                        progressDialog.dismiss();
                        context.finish();
                    }
                });
    }


}
