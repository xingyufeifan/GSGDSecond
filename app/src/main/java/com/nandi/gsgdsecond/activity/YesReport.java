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
import com.nandi.gsgdsecond.bean.DailyBean;
import com.nandi.gsgdsecond.bean.DailyLogInfo;
import com.nandi.gsgdsecond.greendao.GreenDaoHelper;
import com.nandi.gsgdsecond.utils.Constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by qingsong on 2017/11/25.
 */

public class YesReport extends Fragment {

    @BindView(R.id.data_show)
    RecyclerView dataShow;
    @BindView(R.id.refresh_show)
    SwipeRefreshLayout refreshShow;
    private DailyListAdapter listAdapter;
    private List<DailyLogInfo> listBean;
    Unbinder unbinder;
    public static final int DANGER_REQUEST_CODE=202;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yes, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        refresh();
        return view;
    }
    /**
     * 刷新当前界面
     */
    private void refresh() {
        refreshShow.setColorSchemeResources(R.color.colorPrimary);
        refreshShow.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initView();
                refreshShow.setRefreshing(false);
            }
        });
    }
    private void initView() {
        listBean = new ArrayList<>();
        listBean = GreenDaoHelper.queryDailyLogInfoList();
        Log.d("qs", "initView: "+listBean);
        dataShow.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new DailyListAdapter(getContext(), listBean);
        listAdapter.setOnItemClickListener(new DailyListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), DailyReportActivity.class);
                intent.putExtra(Constant.DISASTER, (Serializable) listBean.get(position));
                intent.putExtra("type",1);
                startActivityForResult(intent, DANGER_REQUEST_CODE);
            }
        });
        dataShow.setAdapter(listAdapter);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==DANGER_REQUEST_CODE){
            initView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
