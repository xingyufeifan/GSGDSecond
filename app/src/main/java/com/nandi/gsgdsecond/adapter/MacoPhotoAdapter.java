package com.nandi.gsgdsecond.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.DailyLogInfo;
import com.nandi.gsgdsecond.bean.DisasterUpInfo;
import com.nandi.gsgdsecond.utils.ToastUtils;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by qingsong on 2017/12/1.
 */

public class MacoPhotoAdapter extends RecyclerView.Adapter<MacoPhotoAdapter.MyViewHolder> {
    private Context mContext;
    public MacoPhotoAdapter.OnItemClickListener mOnItemClickListener;
    private String[] listBeans;

    public MacoPhotoAdapter(Context context, String[] listBeans) {
        mContext = context;
        this.listBeans = listBeans;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(MacoPhotoAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public MacoPhotoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.maco_photo, null);
        MacoPhotoAdapter.MyViewHolder holderA = new MacoPhotoAdapter.MyViewHolder(view);
        return holderA;
    }

    @Override
    public void onBindViewHolder(MacoPhotoAdapter.MyViewHolder holder, final int position) {
        if (!TextUtils.isEmpty(listBeans[position].toString().trim())){

            Glide.with(mContext).load("http://183.230.108.112/meteor/downImage.do?imageName="+listBeans[position])
                    .placeholder(R.drawable.downloading).error(R.drawable.download_pass).into(holder.macroPhoto);
        }

        if (mOnItemClickListener != null) {

            holder.macroPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listBeans.length;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView macroPhoto;


        public MyViewHolder(View itemView) {
            super(itemView);
            macroPhoto = (ImageView) itemView.findViewById(R.id.macoPhoto);
        }
    }
}