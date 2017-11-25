package com.nandi.gsgdsecond.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by qingsong on 2017/11/25.
 */

public class DailyFragmentAdapter extends FragmentPagerAdapter {
    private String[] mTitles = new String[]{"未上报", "已上报"};
    private List<Fragment>list;

    public DailyFragmentAdapter(FragmentManager fm,List<Fragment>list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    //用来设置tab的标题
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}

