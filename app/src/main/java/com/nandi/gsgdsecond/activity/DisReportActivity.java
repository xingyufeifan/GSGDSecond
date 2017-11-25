package com.nandi.gsgdsecond.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.utils.CommonUtils;
import com.nandi.gsgdsecond.utils.MyProgressBar;
import com.nandi.gsgdsecond.utils.PictureUtils;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 驻守人员：灾情速报界面
 * 文本与图片分开上传，2个接口 先传文本，再传图片。
 * 传图片时，同时传phoneNum,phoneID,时间,服务器那边去关联文本数据库
 * Created by baohongyan on 2017/11/23.
 */

public class DisReportActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_CAMERA = 2;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_call)
    ImageView ivCall;
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
    private File out;  //图片保存的文件路径
    private List<Bitmap> imgList = new ArrayList<Bitmap>(); //图片信息
    private Map<String, String> map;  //文本信息
    private List<String> imgFileList = new ArrayList<String>();
    private DatePicker datePicker;// 日期控件
    private TimePicker timePicker;// 时间控件
    private AlertDialog dialog;// 选择时间日期对话框
    private String phoneNum;
    private String phoneID;
    private String time;
    private int upnum;

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

    private void initView(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在上传...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressBar = new MyProgressBar(context);
    }

    /**
     * 拍照相关方法
     */
    private void setPhoto(){
        imgAdapter = new ImageAdapter();
        imgAdapter.setIsShowDelete(isShowDelete);
        gvPhoto.setAdapter(imgAdapter);
    }

    /**
     * 拍照响应事件
     */
    private void setListener(){
        gvPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isShowDelete == true){
                    // 如果处于准备删除的状态，单击则删除图标消失
                    isShowDelete = false;
                    imgAdapter.setIsShowDelete(isShowDelete);
                } else {
                    //处于正常状态
                    if (position==imgList.size() && imgList.size()<5){
                        // 添加图片
                        selectImage();
                    }
                    if (position < imgList.size()){
                        //放大图片
                        //TODO
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
    private void selectImage(){
        final CharSequence[] items = {"拍照上传","从相册选择"};
        new AlertDialog.Builder(context).setTitle("请选择图片来源")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == SELECT_PICTURE){
                            getLocalImage();
                        } else {
//                            getCameraImage();
                        }
                    }
                }).create().show();

    }

    /**
     * 选择本地图片
     */
    private void getLocalImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, SELECT_PICTURE);
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
                showDateDialog();
                break;
            case R.id.btn_report:

                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            Cursor cursor;
            switch (requestCode){
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
                    if (null != bm && !"".equals(bm)) {
                        imgList.add(bm);
                    }
                    imgAdapter.notifyDataSetChanged();
                    break;
                case SELECT_CAMERA:  //拍照添加

                    break;
            }
        }
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

    private void showDateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = this.getLayoutInflater().inflate(R.layout.dialog_datetime, null);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker1);
        datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        builder.setTitle("设置时间");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                String month1 = month + "";
                String day1 = day + "";
                String hour1 = hour + "";
                String minute1 = minute + "";
                if (month <= 9) {
                    month1 = 0 + month1;
                }
                if (day <= 9) {
                    day1 = 0 + day1;
                }
                if (hour <= 9) {
                    hour1 = 0 + hour1;
                }
                if (minute <= 9) {
                    minute1 = 0 + minute1;
                }
                tv_disTime.setText(year + "-" + month1 + "-" + day1 + " " + hour1 + ":" + minute1 + ":" + "00");
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }









    /**
     * 用于gridview显示多张照片
     */
    public class ImageAdapter extends BaseAdapter{

        private boolean isDelete; // 用于删除图标的显隐
        private LayoutInflater inflater = LayoutInflater.from(context);

        @Override
        public int getCount() {
            //需要多出一个长度用于显示相机
            return imgList.size()+1;
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
            if (imgList.size() >= 1){
                if (position <= imgList.size() -1){
                    ll_picparent.setVisibility(View.GONE);
                    img_pic.setVisibility(View.VISIBLE);
                    img_pic.setImageBitmap(imgList.get(position));
                    // 设置删除按钮是否显示
                    delete.setVisibility(isDelete ? View.VISIBLE : View.GONE);
                }
            }
            if (imgList.size() >= 4){
                ll_picparent.setVisibility(View.GONE);
            }

            // 当处于删除状态时，删除事件可用
            // 注意：必须放到getView这个方法中，放到onitemClick中是不起作用的。
            if (isDelete){
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
         * @param isShowDelete
         */
        public void setIsShowDelete(boolean isShowDelete) {
            this.isDelete = isShowDelete;
            notifyDataSetChanged();
        }
    }



}
