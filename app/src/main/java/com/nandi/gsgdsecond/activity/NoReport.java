package com.nandi.gsgdsecond.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.adapter.DailyListAdapter;
import com.nandi.gsgdsecond.bean.DailyLogInfo;
import com.nandi.gsgdsecond.greendao.GreenDaoHelper;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.SharedUtils;
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
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 驻守日志查看---已上传
 * Created by qingsong on 2017/11/25.
 */

public class NoReport extends Fragment {

    @BindView(R.id.data_show)
    RecyclerView dataShow;
    @BindView(R.id.refresh_show)
    SwipeRefreshLayout refreshShow;
    Unbinder unbinder;
    private String mobile;
    private List<DailyLogInfo> listBean;
    private DailyListAdapter listAdapter;
    public static final int DANGER_REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no, container, false);
        mobile = (String) SharedUtils.getShare(getContext(), Constant.MOBILE, "12");
        unbinder = ButterKnife.bind(this, view);
        request();
        refresh();
        return view;
    }

    private void request() {
        OkHttpUtils.post().url(getString(R.string.base_url)+"listByMobile.do")
                .addParams("mobile", mobile)
                .addParams("type", "1")
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

    private void initView(String data) {
        listBean = new ArrayList<>();
        listBean = getListByArray(data);
        Log.d("qs", "initView: " + listBean);
        dataShow.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new DailyListAdapter(getContext(), listBean);
        listAdapter.setOnItemClickListener(new DailyListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), DailyReportActivity.class);
                intent.putExtra(Constant.DISASTER, (Serializable) listBean.get(position));
                intent.putExtra("type", 2);
                startActivityForResult(intent, DANGER_REQUEST_CODE);
            }
        });
        dataShow.setAdapter(listAdapter);
    }

    public List<DailyLogInfo> getListByArray(String jsonString) {
        listBean = new ArrayList<DailyLogInfo>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                listBean.add(new DailyLogInfo(jsonObject.optLong("id"),
                        jsonObject.optString("record_time"),
                        jsonObject.optString("user_name"),
                        jsonObject.optString("work_type"),
                        jsonObject.optString("situation"),
                        jsonObject.optString("log_content"),
                        jsonObject.optString("remarks")
                ));
            }
            return listBean;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==DANGER_REQUEST_CODE){
           refresh();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
