package com.nandi.gsgdsecond.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.greendao.GreenDaoHelper;
import com.nandi.gsgdsecond.service.LocationService;
import com.nandi.gsgdsecond.utils.CommonUtils;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.DownloadUtils;
import com.nandi.gsgdsecond.utils.MyProgressBar;
import com.nandi.gsgdsecond.utils.SharedUtils;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by baohongyan on 2017/11/21.
 */

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private CloudPushService cloudPushService;
    private AlertDialog dialog;
    private String mobile;
    private MyProgressBar progressBar;
    private Context context;
    public String uploadUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        initGPS();
        bindAccount();
        checkUpdate(0);
    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "请打开GPS",
                    Toast.LENGTH_SHORT).show();
            // 转到手机设置界面，用户设置GPS
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
        }
    }

    public void initData() {
        View headerView = navView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.tv_head_number);
        progressBar = new MyProgressBar(context);
        mobile = (String) SharedUtils.getShare(context, Constant.MOBILE, "");
        textView.setText("当前账号: " + mobile);
    }

    public void setListener() {
        navView.setNavigationItemSelectedListener(this);
    }

    /**
     * 阿里云推送，绑定账号
     */
    private void bindAccount() {
        String number = (String) SharedUtils.getShare(context, Constant.MOBILE, "");
        cloudPushService = PushServiceFactory.getCloudPushService();
        cloudPushService.bindAccount(number, new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d("cp", "绑定账号成功");
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d("cp", "绑定账号失败");
            }
        });
        cloudPushService.turnOnPushChannel(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d("cp", "推送通道打开成功");
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d("cp", "推送通道打开失败");

            }
        });
    }

    /**
     * 检查更新
     *  http://www.cqdzjc.com:8090/meteor/haveNewVersion.do
     *  type --- 0:Android、1:IOS
     * @param tags 0自动更新 1手动更新
     */
    private void checkUpdate(final int tags) {
        OkHttpUtils.get().url(getResources().getString(R.string.update_base_url)+"haveNewVersion.do")
                .addParams("type","0")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject object = new JSONObject(response);
                            int status = object.getInt("status");
                            if (status == 200) {
                                int code = object.getInt("data");
                                if (code > getVerCode(context)) {
                                    showNotice();
                                } else if (code <= getVerCode(context) && tags != 0) {
                                    ToastUtils.showShort(context, "当前没有新版本");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public int getVerCode(Context context) {
        int versionNumber = -1;
        try {
            versionNumber = context.getPackageManager().getPackageInfo("com.nandi.gsgdsecond", 0).versionCode;
            System.out.println("当前版本" + versionNumber);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("version", e.getMessage());
        }
        return versionNumber;
    }

    /**
     * 下载 downloadTelecom 电信
     * downloadLocomotion 移动
     * downloadUnicom 联通
     */
    private void showNotice() {
        new AlertDialog.Builder(context)
                .setTitle("更新提示")
                .setIcon(R.drawable.warning)
                .setMessage("发现新版本，是否立即下载？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DownloadUtils(context).downloadAPK(getResources().getString(R.string.update_base_url)+"downloadUnicom.do", "app_gsgd" + getVerCode(context) + ".apk");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_update) {
            checkUpdate(1);
        } else if (id == R.id.nav_setting) {
            startActivityForResult(new Intent(context, VideoConfig.class), 1);
        } else if (id == R.id.nav_clear) {
            clearFile();
        } else if (id == R.id.nav_login_out) {
            dialog = new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setIcon(R.drawable.warning)
                    .setMessage("确定要注销当前账号吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loginOut();
                            stopService(new Intent(context, LocationService.class));
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }else if ((id == R.id.nav_scanning)){
            Intent intent = new Intent(context, CaptureActivity.class);
            startActivityForResult(intent, 101);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void clearFile() {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setIcon(R.drawable.warning)
                .setMessage("确定要清除保存的数据、照片吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        GreenDaoHelper.deleteAllInfo();
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
                        if (file.isDirectory()) {
                            File[] files = file.listFiles();
                            for (File f : files) {
                                f.delete();
                            }
                        } else if (file.exists()) {
                            file.delete();
                        }
                        //清空所有share
                        SharedUtils.clearShare(context);
                        ToastUtils.showLong(context, "清除成功");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void loginOut() {
        cloudPushService.unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d("cp", "解绑账号成功");
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d("cp", "解绑账号失败");

            }
        });
        cloudPushService.turnOffPushChannel(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d("cp", "通道关闭成功");

            }

            @Override
            public void onFailed(String s, String s1) {
                Log.d("cp", "通道关闭失败");

            }
        });
        GreenDaoHelper.deleteAllInfo();
        //清空所有share
        SharedUtils.clearShare(context);
        startActivity(new Intent(context, LoginActivity.class));
        dialog.dismiss();
        finish();
    }

    @OnClick({R.id.iv_menu, R.id.iv_call})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.iv_call:
                getNumber();
                break;
                default:
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
                .setIcon(R.drawable.warning)
                .setMessage("是否要发起电话帮助？")
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(context)
                    .setTitle("提示")
                    .setIcon(R.drawable.warning)
                    .setMessage("确定要退出程序吗？")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            ToastUtils.showShort(context, "设置成功");
        }
    }

}
