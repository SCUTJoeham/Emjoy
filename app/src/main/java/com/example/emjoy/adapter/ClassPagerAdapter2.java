package com.example.emjoy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.emjoy.fragment.DongmanFragment;
import com.example.emjoy.fragment.MingxingFragment;
import com.example.emjoy.fragment.RemenFragment;
import com.example.emjoy.fragment.XiongmaotouFragment;

import java.util.ArrayList;

public class ClassPagerAdapter2 extends FragmentPagerAdapter {
    private ArrayList<String> mTitleArray; // 声明一个标题文字队列

    // 碎片页适配器的构造函数，传入碎片管理器与标题队列
    public ClassPagerAdapter2(FragmentManager fm, ArrayList<String> titleArray) {
        super(fm);
        mTitleArray = titleArray;
    }

    // 获取指定位置的碎片Fragment
    public Fragment getItem(int position) {
        if (position == 0) { // 第一页展示热门表情
            return new DongmanFragment();
        } else if (position == 1) { // 第二页展示明星表情
            return new MingxingFragment();
        } else if (position == 2) { // 第三页展示动漫表情
            return new DongmanFragment();
        } else if (position == 3) { // 第四页展示熊猫头表情
            return new XiongmaotouFragment();
        }
        return new RemenFragment();
    }

    // 获取碎片Fragment的个数
    public int getCount() {
        return mTitleArray.size();
    }

    // 获得指定碎片页的标题文本
    public CharSequence getPageTitle(int position) {
        return mTitleArray.get(position);
    }
}
