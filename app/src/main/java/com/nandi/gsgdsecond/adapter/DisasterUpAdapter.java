package com.nandi.gsgdsecond.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.DisasterUpInfo;

import java.util.List;

/**
 * Created by ChenPeng on 2017/12/1.
 */

public class DisasterUpAdapter extends RecyclerView.Adapter<DisasterUpAdapter.DisasterUpViewHolder> {
    private Context context;
    private List<DisasterUpInfo> disasterUpInfo;
    private OnItemClickListener listener;

    public DisasterUpAdapter(Context context, List<DisasterUpInfo> disasterUpInfo) {
        this.context = context;
        this.disasterUpInfo = disasterUpInfo;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public DisasterUpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_disaster_view,parent, false);
        return new DisasterUpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DisasterUpViewHolder holder, int position) {
        holder.tvName.setText(disasterUpInfo.get(position).getDis_name());
        holder.tvTime.setText(disasterUpInfo.get(position).getU_time());
        holder.TvType.setText(disasterUpInfo.get(position).getMacro_data());
    }

    @Override
    public int getItemCount() {
        return disasterUpInfo.size();
    }

    class DisasterUpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName, tvTime, TvType;

        DisasterUpViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            TvType = (TextView) itemView.findViewById(R.id.tv_type);
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
