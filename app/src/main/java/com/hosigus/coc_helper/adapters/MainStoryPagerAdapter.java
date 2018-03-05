package com.hosigus.coc_helper.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hosigus.coc_helper.fragments.StoryFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某只机智 on 2018/2/13.
 * 主页面page中的home页内page的adapter
 */

public class MainStoryPagerAdapter extends FragmentPagerAdapter {
    private List<StoryFragment> fragmentList;
    private List<String> title;

    public MainStoryPagerAdapter(FragmentManager fm,List<StoryFragment> fl) {
        super(fm);
        fragmentList=fl;
        title = new ArrayList<>();
        title.add("我的");
        title.add("最新");
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
