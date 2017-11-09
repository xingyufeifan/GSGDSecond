package com.nandi.gsgdsecond.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by ChenPeng on 2017/10/11.
 */

public class MyProgressBar {
    private ProgressDialog dialog;
    public MyProgressBar(Context context) {
        dialog= new ProgressDialog(context,ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }
    public void show(String msg){
        dialog.setMessage(msg);
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }
}
