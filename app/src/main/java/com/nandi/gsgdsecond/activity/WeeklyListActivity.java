package com.nandi.gsgdsecond.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.adapter.WeeklyListAdapter;
import com.nandi.gsgdsecond.bean.DailyBean;
import com.nandi.gsgdsecond.utils.CommonUtils;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.MyProgressBar;
import com.nandi.gsgdsecond.utils.SharedUtils;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 片区专管：查看已上报周报页面
 */
public class WeeklyListActivity extends AppCompatActivity {
    public static final int DANGER_REQUEST_CODE = 303;
    @BindView(R.id.data_show)
    RecyclerView dataShow;
    @BindView(R.id.refresh_show)
    SwipeRefreshLayout refreshShow;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.tv_title)
    TextView tv_title;
    private WeeklyListAdapter listAdapter;
    private List<DailyBean> listBean;
    private Context mContext;
    private String mobile;
    private ProgressDialog progressDialog;
    private MyProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_list);
        mContext = this;
        ButterKnife.bind(this);
        initData();
        request();
        refresh();
    }

    private void initData() {
        tv_title.setText("已上报周报");
        mobile = (String) SharedUtils.getShare(mContext, Constant.MOBILE, "");
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在上传...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressBar = new MyProgressBar(mContext);
    }

    /**
     * 刷新当前界面
     */
    private void refresh() {
        refreshShow.setColorSchemeResources(R.color.colorPrimary);
        refreshShow.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
                refreshShow.setRefreshing(false);
            }
        });
    }
    private void request() {
        OkHttpUtils.post().url(getString(R.string.base_url)+"listByMobile.do")
                .addParams("mobile", mobile)
                .addParams("type", "2")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        System.out.println("response = " + response);
                        try {
                            JSONObject json = new JSONObject(response);
                            String data = json.optString("data");
                            String message = json.optString("message");
                            String status = json.optString("status");
                            initView(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initView(String data) {
        listBean = new ArrayList<>();
        listBean = getListByArray(data);
        dataShow.setLayoutManager(new LinearLayoutManager(mContext));
        listAdapter = new WeeklyListAdapter(mContext, listBean);
        listAdapter.setOnItemClickListener(new WeeklyListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(mContext, WeeklyReportActivity.class);
                intent.putExtra(Constant.DISASTER, (Serializable) listBean.get(position));
                intent.putExtra("type", 3);
                startActivityForResult(intent, DANGER_REQUEST_CODE);
            }
        });
        dataShow.setAdapter(listAdapter);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==DANGER_REQUEST_CODE){
            refresh();
        }
    }
    public List<DailyBean> getListByArray(String jsonString) {
        listBean = new ArrayList<DailyBean>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                listBean.add(new DailyBean(jsonObject.optInt("id"),
                        jsonObject.optString("user_name"),
                        jsonObject.optString("record_time"),
                        jsonObject.optString("units"),
                        jsonObject.optString("jobContent"),
                        jsonObject.optString("memberName"),
                        jsonObject.optString("user_id")
                ));
            }
            return listBean;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @OnClick({R.id.iv_back, R.id.iv_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_call:
                getNumber();
                break;
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
                        ToastUtils.showLong(mContext, "获取失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressBar.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            CommonUtils.callPhone(message,WeeklyListActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
