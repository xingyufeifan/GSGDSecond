package com.nandi.gsgdsecond.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by qingsong on 2017/4/13.
 * Toast工具类
 */

public class ToastUtils {

    private final static boolean isShow = true;

    private ToastUtils(){
        throw new UnsupportedOperationException("ToastUtils不能被实例化");
    }

    public static void showShort(Context context, CharSequence text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,150);
        toast.show();
    }

    public static void showLong(Context context,CharSequence text) {
        if(isShow)Toast.makeText(context,text,Toast.LENGTH_LONG).show();
    }
}
