package com.nandi.gsgdsecond.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.utils.CommonUtils;
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

public class MacoYesActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.titlebar)
    RelativeLayout titlebar;
    @BindView(R.id.disNum)
    TextView disNum;
    @BindView(R.id.disName)
    TextView disName;
    @BindView(R.id.disTime)
    TextView disTime;
    @BindView(R.id.macroPhenomenon)
    TextView macroPhenomenon;
    @BindView(R.id.photoShow)
    RecyclerView photoShow;
    private MyProgressBar progressBar;
    private MacoYesActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maco_yes);
        ButterKnife.bind(this);
        context = this;
        initView();

    }

    private void initView() {
        tvTitle.setText("巡查已上报");
        progressBar = new MyProgressBar(context);
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
