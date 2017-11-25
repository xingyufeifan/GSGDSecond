package com.nandi.gsgdsecond.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.FileUtils;
import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.adapter.VideoAdapter;
import com.nandi.gsgdsecond.bean.VideoBean;
import com.nandi.gsgdsecond.utils.Constant;
import com.nandi.gsgdsecond.utils.SharedUtils;
import com.nandi.gsgdsecond.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class RecordVideoActivity extends AppCompatActivity {

    private static final int TAKE_VIDEO = 101;
    @BindView(R.id.btn_record)
    Button btnRecord;
    @BindView(R.id.rv_video)
    RecyclerView rvVideo;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    private File videoFile;
    private Context context;
    private List<VideoBean> videoBeans = new ArrayList<>();
    private VideoAdapter adapter;
    private ProgressDialog progressDialog;
    private int count = 0;
    private String mobile;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        ButterKnife.bind(this);
        context = this;
        mobile = (String) SharedUtils.getShare(context, Constant.MOBILE, "");
        type = (String) SharedUtils.getShare(context, Constant.IMEI, "");
        setAdapter();
        setListener();
    }

    private void setListener() {
        adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                playVideo(videoBeans.get(position).getPath());

            }

            @Override
            public void onCheckChange(int position, boolean check) {
                VideoBean videoBean = videoBeans.get(position);
                videoBean.setCheck(check);
                videoBeans.set(position, videoBean);
            }
        });
    }

    private void playVideo(String path) {
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/mp4");
        startActivity(intent);
    }

    private void setAdapter() {
        adapter = new VideoAdapter(context, videoBeans);
        rvVideo.setLayoutManager(new LinearLayoutManager(context));
        rvVideo.setAdapter(adapter);
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在上传...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @OnClick({R.id.btn_record, R.id.btn_upload})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_record:
                recordVideo();
                break;
            case R.id.btn_upload:
                upload();
                break;
        }
    }

    private void upload() {
        List<VideoBean> uploadList = new ArrayList<>();
        for (VideoBean videoBean : videoBeans) {
            if (videoBean.isCheck()) {
                uploadList.add(videoBean);
            }
        }
        if (uploadList.size() > 0) {
            setRequest(uploadList);
            progressDialog.setMax(uploadList.size());
            progressDialog.show();
        } else {
            ToastUtils.showShort(context, "没有需要上传的视频");
        }
    }

    private void setRequest(final List<VideoBean> uploadList) {
        Log.d("cp", "上传文件个数：" + uploadList.size());
        OkHttpUtils.post().url(getString(R.string.base_url)+"saveSurveyVideo.do")
                .addHeader("Content-Type", "multipart/form-data")
                .addParams("mobile", mobile)
                .addParams("type", type)
                .addFile("file", uploadList.get(count).getName(), new File(uploadList.get(count).getPath()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        ToastUtils.showShort(context, "上传失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (count == uploadList.size() - 1) {
                            progressDialog.setProgress(uploadList.size());
                            ToastUtils.showShort(context, "上传成功！");
                            videoBeans.removeAll(uploadList);
                            adapter.notifyDataSetChanged();
                            clean(uploadList);
                            Log.d("cp",response);
                            progressDialog.dismiss();
                            count = 0;
                        } else {
                            count++;
                            progressDialog.setProgress(count);
                            setRequest(uploadList);
                        }
                    }
                });
    }

    private void clean(List<VideoBean> uploadList) {
        for (VideoBean videoBean : uploadList) {
            FileUtils.deleteFile(new File(videoBean.getPath()));
        }
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoFile = new File(createFileDir("Video"), new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".mp4");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //针对Android7.0，需要通过FileProvider封装过的路径，提供给外部调用
            uri = FileProvider.getUriForFile(context, "com.nandi.gsgdsecond.fileprovider", videoFile);//通过FileProvider创建一个content类型的Uri，进行封装
        } else { //7.0以下，如果直接拿到相机返回的intent值，拿到的则是拍照的原图大小，很容易发生OOM，所以我们同样将返回的地址，保存到指定路径，返回到Activity时，去指定路径获取，压缩图片
            uri = Uri.fromFile(videoFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        startActivityForResult(intent, TAKE_VIDEO);
    }

    private File createFileDir(String dir) {
        String path = Environment.getExternalStorageDirectory() + "/" + dir;
        boolean orExistsDir = FileUtils.createOrExistsDir(path);
        if (orExistsDir) {
            return new File(path);
        } else {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_VIDEO) {
                VideoBean videoBean = new VideoBean();
                videoBean.setCheck(false);
                String path = videoFile.getAbsolutePath();
                String[] split = path.split("/");
                videoBean.setName(split[split.length - 1]);
                videoBean.setPath(path);
                videoBeans.add(videoBean);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
