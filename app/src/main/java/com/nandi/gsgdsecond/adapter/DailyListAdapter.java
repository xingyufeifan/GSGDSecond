package com.nandi.gsgdsecond.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.bean.DailyBean;
import com.nandi.gsgdsecond.bean.DailyLogInfo;

import java.util.List;

/**
 * @author qingsong  on 2017/10/26.
 */


public class DailyListAdapter extends RecyclerView.Adapter<DailyListAdapter.MyViewHolder> {
    private Context mContext;
    public DailyListAdapter.OnItemClickListener mOnItemClickListener;
    private List<DailyLogInfo> listBeans;

    public DailyListAdapter(Context context, List<DailyLogInfo> listBeans) {
        mContext = context;
        this.listBeans = listBeans;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(DailyListAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public DailyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_daily_list, parent,false);
        DailyListAdapter.MyViewHolder holderA = new DailyListAdapter.MyViewHolder(view);
        return holderA;
    }

    @Override
    public void onBindViewHolder(DailyListAdapter.MyViewHolder holder, final int position) {
        holder.person.setText(listBeans.get(position).getId() + "");
        holder.time.setText(listBeans.get(position).getTime());
        holder.name.setText(listBeans.get(position).getUserName());
        if (mOnItemClickListener != null) {

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listBeans.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView person;
        public TextView name;
        public LinearLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.time);
            person = (TextView) itemView.findViewById(R.id.person);
            name = (TextView) itemView.findViewById(R.id.name);
            layout = (LinearLayout) itemView.findViewById(R.id.linear);
        }
    }
}
