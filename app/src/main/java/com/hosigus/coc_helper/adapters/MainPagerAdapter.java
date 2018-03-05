package com.hosigus.coc_helper.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by 某只机智 on 2018/2/13.
 * 主页面内page的adapter
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter{
    List<Fragment> pageList;

    public MainPagerAdapter(FragmentManager fm,List<Fragment> pageList) {
        super(fm);
        this.pageList=pageList;
    }

    @Override
    public Fragment getItem(int position) {
        return pageList.get(position);
    }

    @Override
    public int getCount() {
        return pageList.size();
    }
}
