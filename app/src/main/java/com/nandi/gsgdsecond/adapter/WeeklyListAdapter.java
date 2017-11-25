package com.nandi.gsgdsecond.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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


public class WeeklyListAdapter extends RecyclerView.Adapter<WeeklyListAdapter.MyViewHolder> {
    private Context mContext;
    public WeeklyListAdapter.OnItemClickListener mOnItemClickListener;
    private List<DailyBean> listBeans;

    public WeeklyListAdapter(Context context, List<DailyBean> listBeans) {
        mContext = context;
        this.listBeans = listBeans;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(WeeklyListAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public WeeklyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_daily_list, parent,false);
        WeeklyListAdapter.MyViewHolder holderA = new WeeklyListAdapter.MyViewHolder(view);
        return holderA;
    }

    @Override
    public void onBindViewHolder(WeeklyListAdapter.MyViewHolder holder, final int position) {
        if (mOnItemClickListener != null) {

            holder.person.setText(listBeans.get(position).getUser_id());
            holder.name.setText(listBeans.get(position).getUser_name());
            holder.time.setText(listBeans.get(position).getRecord_time());
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
