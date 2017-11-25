package com.nandi.gsgdsecond.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.nandi.gsgdsecond.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取系统时间、打电话、返回等工具类
 * Created by baohongyan on 2017/11/22.
 */

public class CommonUtils {

    public static String getSystemTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String date = sDateFormat.format(new Date(System.currentTimeMillis()));
        return date;
    }

    public static void back(final Activity context, String str){
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setIcon(R.drawable.warning)
                .setMessage(str)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    public static void callPhone(final String message, final Activity context) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setIcon(R.drawable.warning)
                .setMessage("是否发起电话帮助？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + message));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
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
