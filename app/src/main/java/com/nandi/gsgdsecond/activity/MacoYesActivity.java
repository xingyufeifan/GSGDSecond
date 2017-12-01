package com.nandi.gsgdsecond.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.DisasterUpInfo;
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

public class MacoYesActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_call)
    ImageView ivCall;
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
    @BindView(R.id.disValide)
    TextView disValide;
    @BindView(R.id.disState)
    TextView disState;
    private MyProgressBar progressBar;
    private MacoYesActivity context;
    private DisasterUpInfo disasterUpInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maco_yes);
        ButterKnife.bind(this);
        context = this;
        disasterUpInfo = (DisasterUpInfo) getIntent().getSerializableExtra(Constant.DISASTER_UP);
        Log.d("cp", disasterUpInfo.toString());
        initView();
        initData();

    }

    private void initData() {
        disNum.setText(disasterUpInfo.getMacro_id());
        disName.setText(disasterUpInfo.getDis_name());
        disTime.setText(disasterUpInfo.getU_time());
        macroPhenomenon.setText(disasterUpInfo.getMacro_data());
        int is_validate = disasterUpInfo.getIs_validate();
        if (1 == is_validate) {
            disValide.setText("合法");
        } else {
            disValide.setText("不合法");
        }
    }

    private void initView() {
        tvTitle.setText("已上报巡查数据");
        progressBar = new MyProgressBar(context);
        ToastUtils.showLong(disasterUpInfo.getDis_name());
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
