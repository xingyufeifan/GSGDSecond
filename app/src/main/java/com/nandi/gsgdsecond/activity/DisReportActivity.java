package com.nandi.gsgdsecond.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bigkoo.pickerview.TimePickerView;
import com.github.chrisbanes.photoview.PhotoView;
import com.nandi.gsgdsecond.R;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 驻守人员：灾情速报界面
 * Created by baohongyan on 2017/11/23.
 */

public class DisReportActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_CAMERA = 2;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.et_dis_recorder)
    EditText et_disRecorder;
    @BindView(R.id.tv_dis_time)
    TextView tv_disTime;
    @BindView(R.id.gv_photo)
    GridView gvPhoto;
    @BindView(R.id.et_dis_towns)
    EditText et_disTowns;
    @BindView(R.id.et_dis_village)
    EditText et_disVillage;
    @BindView(R.id.et_dis_group)
    EditText et_disGroup;
    @BindView(R.id.et_dis_number)
    EditText et_disNumber;
    @BindView(R.id.et_dis_deathNum)
    EditText et_disDeathNum;
    @BindView(R.id.et_dis_missingNum)
    EditText et_disMissingNum;
    @BindView(R.id.et_dis_injuredNum)
    EditText et_disInjuredNum;
    @BindView(R.id.et_dis_family)
    EditText et_disFamily;
    @BindView(R.id.et_dis_person)
    EditText et_disPerson;
    @BindView(R.id.et_dis_remarks)
    EditText et_disRemarks;

    private DisReportActivity context;
    private ProgressDialog progressDialog;
    private MyProgressBar progressBar;

    private ImageAdapter imgAdapter;
    private boolean isShowDelete = false;  //是否显示删除图标
    private File pictureFile;  //图片保存的文件路径
    private List<Bitmap> imgList = new ArrayList<Bitmap>(); //图片信息
    private Map<String, String> map;  //文本信息
    private List<String> imgFileList = new ArrayList<String>();
    private DatePicker datePicker;// 日期控件
    private TimePicker timePicker;// 时间控件
    private AlertDialog dialog;// 选择时间日期对话框
    private String phoneNum;
    private String imei;
    private int upnum = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dis_report);
        ButterKnife.bind(this);
        context = this;
        initView();
        setPhoto();
        setListener();
    }

    private void initView() {
        tv_title.setText("灾情速报");
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在上传...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressBar = new MyProgressBar(context);
        phoneNum = (String) SharedUtils.getShare(context, Constant.MOBILE, "");
        imei = (String) SharedUtils.getShare(context, Constant.IMEI, "");
        et_disRecorder.setText((String) SharedUtils.getShare(context, Constant.LOGNAME, ""));
    }

    /**
     * 拍照相关方法
     */
    private void setPhoto() {
        imgAdapter = new ImageAdapter();
        imgAdapter.setIsShowDelete(isShowDelete);
        gvPhoto.setAdapter(imgAdapter);
    }

    /**
     * 拍照响应事件
     */
    private void setListener() {
        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isShowDelete == true) {
                    // 如果处于准备删除的状态，单击则删除图标消失
                    isShowDelete = false;
                    imgAdapter.setIsShowDelete(isShowDelete);
                } else {
                    //处于正常状态
                    if (position == imgList.size() && imgList.size() < 5) {
                        // 添加图片
                        selectImage();
                    }
                    if (position < imgList.size()) {
                        //放大图片
                        enlargePhoto(imgList.get(position));
                    }
                }
                imgAdapter.notifyDataSetChanged();
            }
        });

        gvPhoto.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 长按显示删除图标
                if (isShowDelete == false) {
                    isShowDelete = true;
                }
                imgAdapter.setIsShowDelete(isShowDelete);
                return true;
            }
        });
    }

    /**
     * 选择图片来源
     */
    private void selectImage() {
        final CharSequence[] items = {"拍照上传", "从相册选择"};
        new AlertDialog.Builder(context).setTitle("请选择图片来源")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == SELECT_PICTURE) {
                            getLocalImage();
                        } else {
                            checkPermission();
                        }
                    }
                }).create().show();

    }

    /**
     * 选择本地图片
     */
    private void getLocalImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, SELECT_PICTURE);
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
            getCameraImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                int length = grantResults.length;
                final boolean isGranted = length >= 1 && PackageManager.PERMISSION_GRANTED == grantResults[length - 1];
                if (isGranted) {  //如果用户赋予权限，则调用相机
                    getCameraImage();
                } else { //未赋予权限，则做出对应提示
                    com.blankj.utilcode.util.ToastUtils.showShort("请打开照相机权限");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    /**
     * 拍照
     */
    private void getCameraImage() {
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
        startActivityForResult(intent, SELECT_CAMERA);
    }

    /**
     * 放大图片
     */
    private void enlargePhoto(Bitmap bitmap) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_enlarge_photo, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.pv_image);
        //TODO
        photoView.setImageBitmap(bitmap);
        new AlertDialog.Builder(context, R.style.Transparent)
                .setView(view)
                .show();
    }

    @OnClick({R.id.iv_back, R.id.iv_call, R.id.tv_dis_time, R.id.btn_report})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                back();
                break;
            case R.id.iv_call:
                getNumber();
                break;
            case R.id.tv_dis_time: //选择时间
                new TimePickerView.Builder(context, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) { //选中事件回调
                        tv_disTime.setText(getTime(date));
                    }
                }).setSubmitText("确定").setCancelText("取消").build().show();
                break;
            case R.id.btn_report:
                if (checkEditText()) {
                    ToastUtils.showShort(context, "请输入完整信息！");
                } else {
                    reportText(new Api(context).getDisReportTextUrl());
                }
                break;
        }
    }

    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Cursor cursor;
            switch (requestCode) {
                case SELECT_PICTURE: //从相册选择
                    Uri vUri = data.getData();
                    // 将图片内容解析成字节数组
                    String[] proj = {MediaStore.Images.Media.DATA};
                    cursor = context.getContentResolver().query(vUri, proj, null, null, null);
                    int column_index = cursor.getColumnIndex(proj[0]);
                    cursor.moveToFirst();
                    String path1 = cursor.getString(column_index);
                    cursor.close();
                    cursor = null;
                    imgFileList.add(path1);
                    Bitmap bm = PictureUtils.getxtsldraw(context, path1);
                    if (null != bm) {
                        imgList.add(bm);
                    }
                    imgAdapter.notifyDataSetChanged();
                    break;
                case SELECT_CAMERA:  //拍照添加
                    Log.d("Camera", pictureFile.getAbsolutePath());
                    imgFileList.add(pictureFile.getAbsolutePath());
                    Bitmap bm1 = PictureUtils.getxtsldraw(context, pictureFile.getAbsolutePath());
                    if (null != bm1) {
                        bm1 = compressImage(bm1);
                        imgList.add(bm1);
                    }
                    imgAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 压缩图片到合适大小
     *
     * @param image
     * @return
     */
    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    @Override
    public void onBackPressed() {
        back();
    }

    /**
     * 返回上一级
     */
    private void back() {
        CommonUtils.back(context, "确定要退出灾情填写吗？");
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
        if (et_disRecorder.getText().toString().trim().length() == 0 ||
                tv_disTime.getText().toString().trim().length() == 0 ||
                et_disTowns.getText().toString().trim().length() == 0 ||
                et_disVillage.getText().toString().trim().length() == 0 ||
                et_disGroup.getText().toString().trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 上传灾情文本信息
     * userName：记录人 happenTime：时间 township：乡/镇 village：村 group：组 disasterNum：受灾人数
     * dieNum：死亡人数 missingNum：失踪人数 injuredNum：受伤人数 houseNum：潜在威胁户
     * peopleNum：人 notes：备注
     */
    private void reportText(String url) {
        progressDialog.show();
        PostFormBuilder formBuilder = OkHttpUtils.post().url(url)
                .addHeader("Content-Type", "multipart/form-data")
                .addParams("phoneNum", phoneNum)
                .addParams("phoneID", imei)
                .addParams("userName", et_disRecorder.getText().toString().trim())
                .addParams("happenTime", tv_disTime.getText().toString().trim())
                .addParams("township", et_disTowns.getText().toString().trim())
                .addParams("village", et_disVillage.getText().toString().trim())
                .addParams("group", et_disGroup.getText().toString().trim())
                .addParams("disasterNum", et_disNumber.getText().toString().trim())
                .addParams("dieNum", et_disDeathNum.getText().toString().trim())
                .addParams("missingNum", et_disMissingNum.getText().toString().trim())
                .addParams("injuredNum", et_disInjuredNum.getText().toString().trim())
                .addParams("houseNum", et_disFamily.getText().toString().trim())
                .addParams("peopleNum", et_disPerson.getText().toString().trim())
                .addParams("notes", et_disRemarks.getText().toString().trim());

        for (String s : imgFileList) {
            formBuilder.addFile("upload", CommonUtils.getSystemTime() + ".jpg", new File(s));
        }
        formBuilder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(context, "网络连接失败,请稍后重试");
                        Log.d("cp",e.getMessage());
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        System.out.println("response = " + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if ("200".equals(obj.optString("status"))) {
                                    progressDialog.dismiss();
                                    ToastUtils.showShort(context, "上传成功");
                                    clearAllText();
                                    context.finish();
                            } else {
                                progressDialog.dismiss();
                                ToastUtils.showShort(context, "上传失败，请稍后重试!");
                            }
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            ToastUtils.showShort(context, "上传失败，请稍后重试");
                        }
                    }
                });
    }

    /**
     * 上传成功，清除数据
     */
    private void clearAllText() {
        tv_disTime.setText("");
        et_disTowns.setText("");
        et_disVillage.setText("");
        et_disGroup.setText("");
        et_disNumber.setText("");
        et_disDeathNum.setText("");
        et_disMissingNum.setText("");
        et_disInjuredNum.setText("");
        et_disFamily.setText("");
        et_disPerson.setText("");
        et_disRemarks.setText("");
        imgList.clear();
        imgAdapter.notifyDataSetChanged();
    }

    /**
     * 用于gridview显示多张照片
     */
    public class ImageAdapter extends BaseAdapter {

        private boolean isDelete; // 用于删除图标的显隐
        private LayoutInflater inflater = LayoutInflater.from(context);

        @Override
        public int getCount() {
            //需要多出一个长度用于显示相机
            return imgList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return imgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //初始化页面和相关控件
            convertView = inflater.inflate(R.layout.item_gridview_image, null);
            ImageView img_pic = (ImageView) convertView.findViewById(R.id.img_pic);
            LinearLayout ll_picparent = (LinearLayout) convertView.findViewById(R.id.ll_picparent);
            ImageView delete = (ImageView) convertView.findViewById(R.id.img_delete);
            //默认的添加图片的那个item是不需要显示删除图片的
            if (imgList.size() >= 1) {
                if (position <= imgList.size() - 1) {
                    ll_picparent.setVisibility(View.GONE);
                    img_pic.setVisibility(View.VISIBLE);
                    img_pic.setImageBitmap(imgList.get(position));
                    // 设置删除按钮是否显示
                    delete.setVisibility(isDelete ? View.VISIBLE : View.GONE);
                }
            }
            if (imgList.size() >= 4) {
                ll_picparent.setVisibility(View.GONE);
            }

            // 当处于删除状态时，删除事件可用
            // 注意：必须放到getView这个方法中，放到onitemClick中是不起作用的。
            if (isDelete) {
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgList.remove(position);
                        imgFileList.remove(position);
                        imgAdapter.notifyDataSetChanged();
                    }
                });
            }

            return convertView;
        }

        /**
         * 设置是否显示删除图片
         *
         * @param isShowDelete
         */
        public void setIsShowDelete(boolean isShowDelete) {
            this.isDelete = isShowDelete;
            notifyDataSetChanged();
        }
    }

}
