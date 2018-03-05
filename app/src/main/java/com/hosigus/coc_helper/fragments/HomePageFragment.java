package com.hosigus.coc_helper.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hosigus.coc_helper.R;
import com.hosigus.coc_helper.adapters.MainStoryPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某只机智 on 2018/2/13.
 * 主页面page中的home页
 */

public class HomePageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_main_page_home,container,false);
        TabLayout tabLayout = v.findViewById(R.id.tl_main_page_home);
        ViewPager viewPager = v.findViewById(R.id.vp_main_page_home);

        HotStoryFragment hotStoryFragment=new HotStoryFragment();
        MyStoryFragment myStoryFragment=new MyStoryFragment();
        List<StoryFragment> fragmentList = new ArrayList<>();
        fragmentList.add(myStoryFragment);
        fragmentList.add(hotStoryFragment);
        viewPager.setAdapter(new MainStoryPagerAdapter(getFragmentManager(),fragmentList));
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }
}
