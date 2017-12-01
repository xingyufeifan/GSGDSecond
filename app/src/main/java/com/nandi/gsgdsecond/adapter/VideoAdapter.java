package com.nandi.gsgdsecond.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.VideoBean;

import java.util.List;

/**
 * Created by ChenPeng on 2017/11/25.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{
    private Context context;
    private List<VideoBean> list;
    private OnItemClickListener listener;

    public VideoAdapter(Context context, List<VideoBean> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onCheckChange(int position,boolean check);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_video_view,null);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, final int position) {
        holder.tvName.setText(list.get(position).getName());
        boolean check = list.get(position).isCheck();
        Log.d("cp","check:"+check);
        holder.cbCheck.setChecked(check);
        if (listener!=null){
            holder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(position);
                }
            });
            holder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    listener.onCheckChange(position,b);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        CheckBox cbCheck;
        VideoViewHolder(View itemView) {
            super(itemView);
            tvName= (TextView) itemView.findViewById(R.id.tv_name);
            cbCheck= (CheckBox) itemView.findViewById(R.id.cb_check);
        }
    }
}
