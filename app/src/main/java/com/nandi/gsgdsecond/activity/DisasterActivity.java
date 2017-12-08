package com.nandi.gsgdsecond.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.nandi.gsgdsecond.adapter.DisasterTypeAdapter;
import com.nandi.gsgdsecond.bean.DisasterInfo;
import com.nandi.gsgdsecond.bean.DisasterPoint;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 群测群防：宏观观测页面
 */
public class DisasterActivity extends AppCompatActivity {
    @BindView(R.id.rv_disaster_type)
    RecyclerView rvDisasterType;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    @BindView(R.id.et_other)
    EditText etOther;
    @BindView(R.id.ll_other)
    LinearLayout llOther;
    @BindView(R.id.tv_longitude)
    TextView tvLongitude;
    @BindView(R.id.tv_latitude)
    TextView tvLatitude;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.et_disaster_remarks)
    EditText etRemarks;
    private DisasterTypeAdapter adapter;
    private List<DisasterInfo> disasterInfos = new ArrayList<>();
    private DisasterActivity context;
    private File pictureFile;
    private DisasterPoint disasterPoint;
    private int clickPosition;
    private String[] split;
    private PopupWindow popupWindow;
    private LocationClient locationClient;
    private boolean isSave = true;
    private ProgressDialog progressDialog;
    private int count = 0;
    private MyProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_list);
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
                tvLongitude.setText(lon + "");
                tvLatitude.setText(lat + "");
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

    private void initData() {
        tv_title.setText("宏观观测");
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在上传...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        disasterPoint = (DisasterPoint) getIntent().getSerializableExtra(Constant.DISASTER);
        String disasterType = disasterPoint.getDisasterType();
        split = disasterType.split(",");
        for (String s : split) {
            if ("其他现象".equals(s)) {
                llOther.setVisibility(View.VISIBLE);
            }
        }
        etOther.setText((String) SharedUtils.getShare(context, disasterPoint.getNumber() + "other", ""));
        etRemarks.setText((String) SharedUtils.getShare(context, disasterPoint.getNumber() + "remark", ""));
    }

    private void setListener() {
        adapter.setOnItemViewClickListener(new DisasterTypeAdapter.onItemViewClickListener() {
            @Override
            public void onTakePhotoClick(int position) {
                checkPermission(position);
            }

            @Override
            public void onPhotoClick(int position) {
                if (disasterInfos.get(position).getPhotoPath() != null) {
                    showOrDelete(position);
                }
            }

            @Override
            public void onCheckedChange(int position, boolean b) {
                Log.d("cp", "position:" + position + "/" + b);
                isSave = false;
                DisasterInfo disasterInfo = disasterInfos.get(position);
                disasterInfo.setFind(b);
                if (!b) {
                    if (disasterInfo.getPhotoPath() != null) {
                        File file = new File(disasterInfo.getPhotoPath());
                        if (file.exists()) {
                            file.delete();
                        }
                        disasterInfo.setPhotoPath(null);
                    }
                }
                disasterInfos.set(position, disasterInfo);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showOrDelete(final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_view, null);
        TextView tvEnlarge = (TextView) view.findViewById(R.id.tv_enlarge);
        TextView tvDelete = (TextView) view.findViewById(R.id.tv_delete);
        tvEnlarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enlargePhoto(position);
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePhoto(position);
            }
        });
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_disaster_list, null);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                popupWindow.dismiss();
                return false;
            }
        });
    }

    private void deletePhoto(final int position) {
        popupWindow.dismiss();
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setIcon(R.drawable.warning)
                .setMessage("确定要删除图片吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isSave = false;
                        DisasterInfo disasterInfo = disasterInfos.get(position);
                        disasterInfo.setFind(false);
                        disasterInfo.setPhotoPath(null);
                        disasterInfos.set(position, disasterInfo);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void enlargePhoto(int position) {
        popupWindow.dismiss();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_enlarge_photo, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.pv_image);
        photoView.setImageBitmap(PictureUtils.getSmallBitmap(disasterInfos.get(position).getPhotoPath(), 600, 1200));
        new AlertDialog.Builder(context, R.style.Transparent)
                .setView(view)
                .show();
    }

    //权限申请
    private void checkPermission(int position) {
        clickPosition = position;
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
        switch (requestCode) {  //申请权限的返回值
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

    private void save() {
        isSave = true;
        SharedUtils.putShare(context, disasterPoint.getNumber() + "other", etOther.getText().toString().trim());
        SharedUtils.putShare(context, disasterPoint.getNumber() + "lon", tvLongitude.getText().toString().trim());
        SharedUtils.putShare(context, disasterPoint.getNumber() + "lat", tvLatitude.getText().toString().trim());
        SharedUtils.putShare(context, disasterPoint.getNumber() + "remark", etRemarks.getText().toString().trim());
        for (DisasterInfo disasterInfo : disasterInfos) {
            GreenDaoHelper.insertDisasterInfo(disasterInfo);
        }
        ToastUtils.showShort(context, "保存成功");
    }

    private void showPhoto() {
        Log.d("cp", pictureFile.length() + "");
        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
        PictureUtils.compressImageToFile(bitmap, pictureFile);
        Log.d("cp", pictureFile.length() + "");
        isSave = false;
        DisasterInfo disasterInfo = disasterInfos.get(clickPosition);
        disasterInfo.setPhotoPath(pictureFile.getAbsolutePath());
        disasterInfo.setFind(true);
        disasterInfos.set(clickPosition, disasterInfo);
        adapter.notifyDataSetChanged();
    }

    private void setAdapter() {
        adapter = new DisasterTypeAdapter(disasterInfos, this);
        rvDisasterType.setLayoutManager(new LinearLayoutManager(this));
        rvDisasterType.setAdapter(adapter);
    }

    private void initView() {
        progressBar = new MyProgressBar(context);
        List<DisasterInfo> queryList = GreenDaoHelper.queryDisasterInfoByNumber(disasterPoint.getNumber());
        if (queryList == null || queryList.size() == 0) {
            for (String s : split) {
                disasterInfos.add(new DisasterInfo(null, s, false, null, disasterPoint.getNumber()));
            }
        } else {
            disasterInfos.addAll(queryList);
        }
        setAdapter();
    }

    @OnClick({R.id.btn_save, R.id.btn_upload, R.id.iv_back, R.id.iv_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_upload:
                if (!"".equals(SharedUtils.getShare(context, disasterPoint.getNumber() + "lon", ""))) {
                    upload();
                } else {
                    ToastUtils.showShort(context, "第一次上传请先保存");
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

    private void upload() {
        List<DisasterInfo> uploadInfos = new ArrayList<>();
        List<Map<String, String>> params = new ArrayList<>();
        List<File> files = new ArrayList<>();
        String otherThings = etOther.getText().toString().trim();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String serialNo = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        for (DisasterInfo disasterInfo : disasterInfos) {
            if (disasterInfo.getFind()) {
                uploadInfos.add(disasterInfo);
            }
        }
        if (uploadInfos.size() > 0) {
            for (DisasterInfo uploadInfo : uploadInfos) {
                Map<String, String> param = new HashMap<>();
                param.put("macroscopicPhenomenon", uploadInfo.getName());
                param.put("monitorType", "宏观观测");
                param.put("otherPhenomena", otherThings);
                param.put("monPointDate", currentDate);
                param.put("unifiedNumber", uploadInfo.getNumber());
                param.put("mobile", (String) SharedUtils.getShare(context, Constant.MOBILE, ""));
                param.put("count", String.valueOf(uploadInfos.size()));
                param.put("xpoint", (String) SharedUtils.getShare(context, disasterPoint.getNumber() + "lon", ""));
                param.put("ypoint", (String) SharedUtils.getShare(context, disasterPoint.getNumber() + "lat", ""));
                param.put("serialNo", serialNo);
                param.put("remarks", etRemarks.getText().toString().trim());
                params.add(param);
                if (uploadInfo.getPhotoPath() == null) {
                    files.add(null);
                } else {
                    files.add(new File(uploadInfo.getPhotoPath()));
                }
            }
        } else {
            if (!TextUtils.isEmpty(otherThings)) {
                Map<String, String> param = new HashMap<>();
                param.put("macroscopicPhenomenon", "其他现象");
                param.put("monitorType", "宏观观测");
                param.put("otherPhenomena", otherThings);
                param.put("monPointDate", currentDate);
                param.put("unifiedNumber", disasterPoint.getNumber());
                param.put("mobile", (String) SharedUtils.getShare(context, Constant.MOBILE, ""));
                param.put("count", String.valueOf(uploadInfos.size()));
                param.put("xpoint", (String) SharedUtils.getShare(context, disasterPoint.getNumber() + "lon", ""));
                param.put("ypoint", (String) SharedUtils.getShare(context, disasterPoint.getNumber() + "lat", ""));
                param.put("serialNo", serialNo);
                param.put("remarks", etRemarks.getText().toString().trim());
                params.add(param);
                files.add(null);
            } else {
                Map<String, String> param = new HashMap<>();
                param.put("macroscopicPhenomenon", "无异常");
                param.put("monitorType", "宏观观测");
                param.put("otherPhenomena", "");
                param.put("monPointDate", currentDate);
                param.put("unifiedNumber", disasterPoint.getNumber());
                param.put("mobile", (String) SharedUtils.getShare(context, Constant.MOBILE, ""));
                param.put("count", String.valueOf(uploadInfos.size()));
                param.put("xpoint", (String) SharedUtils.getShare(context, disasterPoint.getNumber() + "lon", ""));
                param.put("ypoint", (String) SharedUtils.getShare(context, disasterPoint.getNumber() + "lat", ""));
                param.put("serialNo", serialNo);
                param.put("remarks", etRemarks.getText().toString().trim());
                params.add(param);
                files.add(null);
            }
        }
        setPost(params, files);
        progressDialog.setMax(params.size());
        progressDialog.show();
    }

    private void setPost(final List<Map<String, String>> params, final List<File> files) {
        Log.d("cp", "开始请求");
        Log.d("params:", params.toString());
        PostFormBuilder builder;
        PostFormBuilder formBuilder = OkHttpUtils.post().url(new Api(context).getUploadUrl())
                .addHeader("Content-Type", "multipart/form-data")
                .params(params.get(count));
        if (files.get(count) != null) {
            builder = formBuilder.addFile("uploads", files.get(count).getName(), files.get(count));
        } else {
            builder = formBuilder;
        }
        builder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("error", e.toString());
                        progressDialog.dismiss();
                        if (count <= params.size() - 1) {
                            uploadAgain(params, files);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("cp", "成功返回：" + response);
                        if (count == params.size() - 1) {
                            progressDialog.setProgress(params.size());
                            ToastUtils.showShort(context, "上传成功！");
                            progressDialog.dismiss();
                            count = 0;
                            deleteDao();
                            finish();
                        } else {
                            count++;
                            progressDialog.setProgress(count);
                            setPost(params, files);
                        }
                    }
                });
    }

    private void uploadAgain(final List<Map<String, String>> params, final List<File> files) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setIcon(R.drawable.warning)
                .setMessage("上传失败，是否重新上传？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("cp", "again/count:" + count);
                        progressDialog.setProgress(count);
                        progressDialog.show();
                        setPost(params, files);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        count = 0;
                    }
                }).show();
    }

    private void deleteDao() {
        List<DisasterInfo> disasterInfos = GreenDaoHelper.queryDisasterInfoByNumber(disasterPoint.getNumber());
        if (disasterInfos != null) {
            GreenDaoHelper.deleteDisasterInfo(disasterInfos);
        }
        SharedUtils.removeShare(context, disasterPoint.getNumber() + "other");
        SharedUtils.removeShare(context, disasterPoint.getNumber() + "lon");
        SharedUtils.removeShare(context, disasterPoint.getNumber() + "lat");
        SharedUtils.removeShare(context, disasterPoint.getNumber() + "remark");
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        if (isSave) {
            finish();
        } else {
            CommonUtils.back(context, "检测到修改了数据未保存，确定要退出吗？");
        }
    }
}
