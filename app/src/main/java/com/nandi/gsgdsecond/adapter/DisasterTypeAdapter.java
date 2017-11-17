package com.nandi.gsgdsecond.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.DisasterInfo;
import com.nandi.gsgdsecond.utils.PictureUtils;

import java.util.List;

/**
 * Created by ChenPeng on 2017/10/16.
 */

public class DisasterTypeAdapter extends RecyclerView.Adapter<DisasterTypeAdapter.DisasterTypeViewHolder> {
    private List<DisasterInfo> disasterInfos;
    private Context context;
    private onItemViewClickListener onItemViewClickListener;
    private boolean[] flag;
    public DisasterTypeAdapter(List<DisasterInfo> disasterInfos, Context context) {
        this.disasterInfos = disasterInfos;
        this.context = context;
        this.flag=new boolean[disasterInfos.size()];
    }

    @Override
    public DisasterTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_monitor_info, parent, false);
        return new DisasterTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DisasterTypeViewHolder holder, final int position) {
        holder.tvName.setText(disasterInfos.get(position).getName());
        String photoPath = disasterInfos.get(position).getPhotoPath();
        if (photoPath == null) {
            holder.ivPhoto.setImageResource(R.drawable.showimage);
        } else {
            holder.ivPhoto.setImageBitmap(PictureUtils.getSmallBitmap(disasterInfos.get(position).getPhotoPath(), 100, 100));
        }
        holder.cbIsFind.setOnCheckedChangeListener(null);
        holder.cbIsFind.setChecked(flag[position]);
        boolean find = disasterInfos.get(position).getFind();
        holder.cbIsFind.setChecked(find);
        if (onItemViewClickListener != null) {
            holder.ivTakePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemViewClickListener.onTakePhotoClick(position);
                }
            });
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemViewClickListener.onPhotoClick(position);
                }
            });
            holder.cbIsFind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    onItemViewClickListener.onCheckedChange(position, b);
                    flag[position]=b;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return disasterInfos.size();
    }

    class DisasterTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto, ivTakePhoto;
        CheckBox cbIsFind;
        TextView tvName;

        public DisasterTypeViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            ivTakePhoto = (ImageView) itemView.findViewById(R.id.iv_take_photo);
            cbIsFind = (CheckBox) itemView.findViewById(R.id.cb_is_find);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

    public interface onItemViewClickListener {
        void onTakePhotoClick(int position);

        void onPhotoClick(int position);

        void onCheckedChange(int position, boolean b);
    }

    public void setOnItemViewClickListener(onItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }
}
