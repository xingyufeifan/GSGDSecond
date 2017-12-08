package com.nandi.gsgdsecond.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.github.chrisbanes.photoview.PhotoView;
import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.MonitorInfo;
import com.nandi.gsgdsecond.bean.MonitorPoint;
import com.nandi.gsgdsecond.greendao.GreenDaoHelper;
import com.nandi.gsgdsecond.utils.Api;
import com.nandi.gsgdsecond.utils.CommonUtils;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.MyProgressBar;
import com.nandi.gsgdsecond.utils.PictureUtils;
import com.nandi.gsgdsecond.utils.SharedUtils;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 群测群防：定量监测页面
 */
public class MonitorActivity extends AppCompatActivity {
    @BindView(R.id.tv_disaster_name)
    TextView tvDisasterName;
    @BindView(R.id.tv_monitor_name)
    TextView tvMonitorName;
    @BindView(R.id.et_dilie)
    EditText etDilie;
    @BindView(R.id.tv_happen_time)
    TextView tvHappenTime;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    @BindView(R.id.cb_clear_yuling)
    CheckBox cbClearYuling;
    @BindView(R.id.ll_yuliang)
    LinearLayout llYuliang;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.tv_title)
    TextView tv_title;
    private MonitorPoint monitorPoint;
    private String name;
    private MonitorActivity context;
    private File pictureFile;
    private MonitorInfo monitorInfo;
    private boolean isSave = true;
    private PopupWindow popupWindow;
    private LocationClient locationClient;
    private MyProgressBar progressBar;
    private String monitorType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        ButterKnife.bind(this);
        context = this;
        initData();
        initView();
        setListener();
        initLocation();
    }

    private void initLocation() {
        locationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000 * 2);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new LocationListener());
        locationClient.start();
    }


    private class LocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int locType = bdLocation.getLocType();
            if (locType == BDLocation.TypeOffLineLocation || locType == BDLocation.TypeGpsLocation || locType == BDLocation.TypeNetWorkLocation) {
                double lon = bdLocation.getLongitude();
                double lat = bdLocation.getLatitude();
                monitorInfo.setLongitude(String.valueOf(lon));
                monitorInfo.setLatitude(String.valueOf(lat));
                locationClient.stop();
            }
        }

        @Override
        public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
            if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_GPS) {
                ToastUtils.showShort(context, "请打开GPS");
            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_WIFI) {
                ToastUtils.showShort(context, "建议打开WIFI提高定位经度");
            }
        }
    }

    private void setListener() {
        etDilie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isSave = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void initData() {
        tv_title.setText("定量监测");
        progressBar = new MyProgressBar(context);
        monitorPoint = (MonitorPoint) getIntent().getSerializableExtra(Constant.MONITOR);
        Log.d("cp", "monitor:" + monitorPoint);
        name = getIntent().getStringExtra(Constant.DISASTER_NAME);
        MonitorInfo queryMonitor = GreenDaoHelper.queryMonitorInfoByNumber(this.monitorPoint.getDisasterNumber(), this.monitorPoint.getMonitorNumber());
        if (queryMonitor == null) {
            monitorInfo = new MonitorInfo();
            monitorInfo.setDisasterNumber(monitorPoint.getDisasterNumber());
            monitorInfo.setMonitorNumber(monitorPoint.getMonitorNumber());
            monitorInfo.setDisasterName(name);
            monitorInfo.setMonitorName(monitorPoint.getName());
            monitorInfo.setHappenTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } else {
            monitorInfo = queryMonitor;
            Log.d("cp", monitorInfo.toString());
        }
    }

    private void initView() {
        if ("雨量".equals(monitorPoint.getType())) {
            monitorType = "雨量监测";
            llYuliang.setVisibility(View.VISIBLE);
        } else {
            monitorType = "定量监测";
        }
        tvType.setText(monitorPoint.getMonitorType() + " (" + monitorPoint.getDimension() + "):");
        tvDisasterName.setText(monitorInfo.getDisasterName());
        tvMonitorName.setText(monitorInfo.getMonitorName());
        tvHappenTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        etDilie.setText(monitorInfo.getCrackLength() == null ? "" : monitorInfo.getCrackLength());
        if (monitorInfo.getPhotoPath() != null) {
            ivPhoto.setImageBitmap(PictureUtils.getSmallBitmap(monitorInfo.getPhotoPath(), 200, 200));
            Log.d("cp", "path不为空");
        }
    }

    @OnClick({R.id.iv_photo, R.id.btn_save, R.id.btn_upload, R.id.iv_back,R.id.iv_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_photo:
                clickPhoto();
                break;
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_upload:
                String otherThings = etDilie.getText().toString().trim();
                if (TextUtils.isEmpty(otherThings)){
                    ToastUtils.showShort(context,"信息填写不完整");
                }else {
                    upload(otherThings);
                }
                break;
            case R.id.iv_back:
                back();
                break;
            case R.id.iv_call:
                getNumber();
                break;
        }
    }
    private void getNumber() {
        progressBar.show("正在获取号码");
        OkHttpUtils.get().url(getResources().getString(R.string.base_url)+"getHelpMobile.do")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressBar.dismiss();
                        ToastUtils.showLong(context,"获取失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressBar.dismiss();
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            String message = jsonObject.getString("message");
                            CommonUtils.callPhone(message, context);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void back() {
        if (isSave) {
            finish();
        } else {
            CommonUtils.back(context, "检测到修改了数据未保存，确定要退出吗？");
        }
    }

    private void upload(String otherThings) {
        progressBar.show("正在上传...");
        String serialNo = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        Map<String, String> map = new HashMap<>();
        map.put("serialNo", serialNo);
        map.put("xpoint", monitorInfo.getLongitude());
        map.put("ypoint", monitorInfo.getLatitude());
        map.put("monitorType", monitorType);
        map.put("mobile", (String) SharedUtils.getShare(context, Constant.MOBILE, ""));
        map.put("unifiedNumber", monitorPoint.getDisasterNumber());
        map.put("monPointNumber", monitorPoint.getMonitorNumber());
        map.put("monPointDate", monitorInfo.getHappenTime());
        map.put("resets", cbClearYuling.isChecked() ? "1" : "0");
        map.put("measuredData", otherThings);
        PostFormBuilder builder;
        PostFormBuilder formBuilder = OkHttpUtils.post().url(new Api(context).getUploadUrl())
                .addHeader("Content-Type", "multipart/form-data")
                .params(map);
        if (monitorInfo.getPhotoPath() == null) {
            builder = formBuilder;
        } else {
            builder = formBuilder.addFile("uploads", new File(monitorInfo.getPhotoPath()).getName(), new File(monitorInfo.getPhotoPath()));
        }
        builder.build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showShort(context, "上传失败");
                progressBar.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d("cp", response);
                progressBar.dismiss();
                ToastUtils.showShort(context, "上传成功");
                deleteDao();
                finish();
            }
        });
    }

    private void deleteDao() {
        MonitorInfo monitorInfo = GreenDaoHelper.queryMonitorInfoByNumber(monitorPoint.getDisasterNumber(), monitorPoint.getMonitorNumber());
        if (monitorInfo != null) {
            GreenDaoHelper.deleteMonitorInfo(monitorInfo);
        }
    }

    private void save() {
        isSave = true;
        monitorInfo.setCrackLength(etDilie.getText().toString().trim());
        MonitorInfo query = GreenDaoHelper.queryMonitorInfoByNumber(monitorPoint.getDisasterNumber(), monitorPoint.getMonitorNumber());
        if (query == null) {
            GreenDaoHelper.insertMonitorInfo(monitorInfo);
        } else {
            monitorInfo.setId(query.getId());
            GreenDaoHelper.updateMonitorInfo(monitorInfo);
        }
        ToastUtils.showShort(context, "保存成功");
    }

    private void clickPhoto() {
        if (monitorInfo.getPhotoPath() == null) {
            checkPermission();
        } else {
            enlargeOrDelete();
        }
    }

    private void enlargeOrDelete() {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_view, null);
        TextView tvEnlarge = (TextView) view.findViewById(R.id.tv_enlarge);
        TextView tvDelete = (TextView) view.findViewById(R.id.tv_delete);
        tvEnlarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enlargePhoto();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePhoto();
            }
        });
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_monitor, null);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                popupWindow.dismiss();
                return false;
            }
        });
    }

    private void deletePhoto() {
        popupWindow.dismiss();
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("确定要删除图片吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isSave = false;
                        monitorInfo.setPhotoPath(null);
                        ivPhoto.setImageResource(R.drawable.add_photo);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void enlargePhoto() {
        popupWindow.dismiss();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_enlarge_photo, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.pv_image);
        photoView.setImageBitmap(PictureUtils.getSmallBitmap(monitorInfo.getPhotoPath(), 600, 1200));
        new AlertDialog.Builder(context, R.style.Transparent)
                .setView(view)
                .show();
    }

    //权限申请
    private void checkPermission() {
        final String permission = Manifest.permission.CAMERA;  //相机权限

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {  //先判断是否被赋予权限，没有则申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                    ) {  //给出权限申请说明
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            } else { //直接申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1); //申请权限，可同时申请多个权限，并根据用户是否赋予权限进行判断
            }
        } else {  //赋予过权限，则直接调用相机拍照
            takePhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                int length = grantResults.length;
                final boolean isGranted = length >= 1 && PackageManager.PERMISSION_GRANTED == grantResults[length - 1];
                if (isGranted) {  //如果用户赋予权限，则调用相机
                    takePhoto();
                } else { //未赋予权限，则做出对应提示
                    com.blankj.utilcode.util.ToastUtils.showShort("请打开照相机权限");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pictureFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg");
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //针对Android7.0，需要通过FileProvider封装过的路径，提供给外部调用
            imageUri = FileProvider.getUriForFile(context, "com.nandi.gsgdsecond.fileprovider", pictureFile);//通过FileProvider创建一个content类型的Uri，进行封装
        } else { //7.0以下，如果直接拿到相机返回的intent值，拿到的则是拍照的原图大小，很容易发生OOM，所以我们同样将返回的地址，保存到指定路径，返回到Activity时，去指定路径获取，压缩图片
            imageUri = Uri.fromFile(pictureFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Log.d("cp", "图片保存路径：" + pictureFile.getAbsolutePath());
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                showPhoto();
            }
        }
    }

    private void showPhoto() {
        Bitmap bitmap= BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
        PictureUtils.compressImageToFile(bitmap,pictureFile);
        isSave = false;
        monitorInfo.setPhotoPath(pictureFile.getAbsolutePath());
        ivPhoto.setImageBitmap(PictureUtils.getSmallBitmap(pictureFile.getAbsolutePath(), 200, 200));
    }

    @Override
    public void onBackPressed() {
        back();
    }
}
