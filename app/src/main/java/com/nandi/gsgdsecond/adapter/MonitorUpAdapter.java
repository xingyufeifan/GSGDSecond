package com.nandi.gsgdsecond.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.DisasterUpInfo;
import com.nandi.gsgdsecond.bean.MonitorUpInfo;

import java.util.List;

/**
 * Created by ChenPeng on 2017/12/1.
 */

public class MonitorUpAdapter extends RecyclerView.Adapter<MonitorUpAdapter.MonitorUpViewHolder>{
    private Context context;
    private List<MonitorUpInfo> monitorUpInfos;
    private OnItemClickListener listener;

    public MonitorUpAdapter(Context context, List<MonitorUpInfo> monitorUpInfos) {
        this.context = context;
        this.monitorUpInfos = monitorUpInfos;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public MonitorUpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_monitor_view,parent, false);
        return new MonitorUpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MonitorUpViewHolder holder, int position) {
        holder.tvName.setText(monitorUpInfos.get(position).getMonitor_name());
        holder.tvTime.setText(monitorUpInfos.get(position).getU_time());
        double monitor_data = monitorUpInfos.get(position).getMonitor_data();
        holder.tvData.setText(String.valueOf(monitor_data));
    }

    @Override
    public int getItemCount() {
        return monitorUpInfos.size();
    }

    class MonitorUpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName, tvTime, tvData;

        MonitorUpViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvData = (TextView) itemView.findViewById(R.id.tv_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClick(view);
            }
        }
    }
}
