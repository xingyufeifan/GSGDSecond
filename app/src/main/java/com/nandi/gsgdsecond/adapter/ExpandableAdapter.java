package com.nandi.gsgdsecond.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nandi.gsgdsecond.R;
import com.nandi.gsgdsecond.activity.DisasterActivity;
import com.nandi.gsgdsecond.activity.DisasterListActivity;
import com.nandi.gsgdsecond.activity.MonitorActivity;
import com.nandi.gsgdsecond.activity.MonitorListActivity;
import com.nandi.gsgdsecond.bean.DisasterPoint;
import com.nandi.gsgdsecond.bean.MonitorPoint;
import com.nandi.gsgdsecond.utils.Constant;

import java.util.List;
import java.util.Map;

/**
 * Created by ChenPeng on 2017/10/16.
 */

public class ExpandableAdapter extends BaseExpandableListAdapter {
    private List<DisasterPoint> disasterPoints;
    private Map<String, List<MonitorPoint>> childMaps;
    private Context context;

    public ExpandableAdapter(List<DisasterPoint> disasterPoints, Map<String, List<MonitorPoint>> childMaps, Context context) {
        this.disasterPoints = disasterPoints;
        this.childMaps = childMaps;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return disasterPoints.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return childMaps.get(disasterPoints.get(i).getNumber()).size();
    }

    @Override
    public Object getGroup(int i) {
        return disasterPoints.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return childMaps.get(disasterPoints.get(i).getNumber()).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        ParentViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_parent_view, viewGroup, false);
            viewHolder = new ParentViewHolder();
            viewHolder.tvParentName = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tvType = (TextView) view.findViewById(R.id.tv_disaster_type);
            viewHolder.tvLon = (TextView) view.findViewById(R.id.tv_longitude);
            viewHolder.tvLat = (TextView) view.findViewById(R.id.tv_latitude);
            viewHolder.ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ParentViewHolder) view.getTag();
        }
        viewHolder.tvParentName.setText(disasterPoints.get(i).getName());
        viewHolder.tvType.setText(disasterPoints.get(i).getType());
        viewHolder.tvLon.setText(disasterPoints.get(i).getLongitude());
        viewHolder.tvLat.setText(disasterPoints.get(i).getLatitude());
        if (b) {
            viewHolder.ivArrow.setImageResource(R.mipmap.ic_down);
        } else {
            viewHolder.ivArrow.setImageResource(R.mipmap.ic_more);
        }
        return view;
    }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_child_view, viewGroup, false);
            viewHolder = new ChildViewHolder();
            viewHolder.tvChildName = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tvWriteView = (TextView) view.findViewById(R.id.writeView);
            viewHolder.nextView = (TextView) view.findViewById(R.id.nextView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) view.getTag();
        }
        if (i1 == 0) {
            viewHolder.tvChildName.setText(childMaps.get(disasterPoints.get(i).getNumber()).get(i1).getName());
            //填报
            viewHolder.tvWriteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DisasterActivity.class);
                    intent.putExtra(Constant.DISASTER, disasterPoints.get(i));
                    context.startActivity(intent);
                }
            });
            //查看
            viewHolder.nextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =  new Intent(context, DisasterListActivity.class);
                    intent.putExtra(Constant.DISASTER_NUMBER,disasterPoints.get(i).getNumber());
                    context.startActivity(intent);
                }
            });
        } else {
            viewHolder.tvChildName.setText("定量监测-" + childMaps.get(disasterPoints.get(i).getNumber()).get(i1).getName());
            //填报
            viewHolder.tvWriteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MonitorActivity.class);
                    intent.putExtra(Constant.MONITOR, childMaps.get(disasterPoints.get(i).getNumber()).get(i1));
                    intent.putExtra(Constant.DISASTER_NAME, disasterPoints.get(i).getName());
                    context.startActivity(intent);
                }
            });
            //查看
            viewHolder.nextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =  new Intent(context, MonitorListActivity.class);
                    intent.putExtra(Constant.DISASTER_NUMBER,disasterPoints.get(i).getNumber());
                    intent.putExtra(Constant.MONITOR_NUMBER,childMaps.get(disasterPoints.get(i).getNumber()).get(i1).getMonitorNumber());
                    context.startActivity(intent);
                }
            });
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private static class ParentViewHolder {
        TextView tvParentName;
        TextView tvType;
        TextView tvLon;
        TextView tvLat;
        ImageView ivArrow;
    }

    private static class ChildViewHolder {
        TextView tvChildName;
        TextView tvWriteView;
        TextView nextView;
    }
}
