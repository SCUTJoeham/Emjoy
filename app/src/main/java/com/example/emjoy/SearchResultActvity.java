
package com.example.emjoy;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.ViewPagerOnTabSelectedListener;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.emjoy.adapter.ClassPagerAdapter;
import com.example.emjoy.adapter.ClassPagerAdapter2;
import com.example.emjoy.util.DateUtil;
import com.example.emjoy.util.MenuUtil;

@SuppressLint("DefaultLocale")
public class SearchResultActvity extends AppCompatActivity {
    private final static String TAG = "HomeActivity";
    private ViewPager vp_content; // 定义一个翻页视图对象
    private TabLayout tab_title; // 定义一个标签布局对象
    private ArrayList<String> mTitleArray = new ArrayList<String>(); // 标题文字队列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 设置空标题
        tl_head.setTitle("");
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);

        mTitleArray.add("搜索结果");
        // 从布局文件中获取名叫vp_content的翻页视图
        vp_content = findViewById(R.id.vp_content);
        initTabLayout(); // 初始化标签布局
        initTabViewPager(); // 初始化标签翻页
    }

    // 初始化标签布局
    private void initTabLayout() {
        // 从布局文件中获取名叫tab_title的标签布局
        tab_title = findViewById(R.id.tab_title);
        // 给tab_title添加一个指定文字的标签
        tab_title.addTab(tab_title.newTab().setText(mTitleArray.get(0)));
        // 给tab_title添加标签选中监听器，该监听器默认绑定了翻页视图vp_content
        tab_title.addOnTabSelectedListener(new ViewPagerOnTabSelectedListener(vp_content));
    }

    // 初始化标签翻页
    private void initTabViewPager() {
        // 构建一个分类信息的翻页适配器
        ClassPagerAdapter2 adapter = new ClassPagerAdapter2(
                getSupportFragmentManager(), mTitleArray);
        // 给vp_content设置分类翻页适配器
        vp_content.setAdapter(adapter);
        // 给vp_content添加页面变更监听器
        vp_content.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 选中tab_title指定位置的标签
                tab_title.getTabAt(position).select();
            }
        });
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        // 显示菜单项左侧的图标
        MenuUtil.setOverflowIconVisible(featureId, menu);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 从menu_home.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) { // 点击了工具栏左边的返回箭头
            finish();
        } else if (id == R.id.menu_search) { // 点击了搜索图标
            // 跳转到搜索页面
            Intent intent = new Intent(this, SearchViewActivity.class);
            intent.putExtra("collapse", false);
            startActivity(intent);
        } else if (id == R.id.menu_refresh) { // 点击了刷新图标
            Toast.makeText(this, "当前刷新时间: " +
                    DateUtil.getNowDateTime("yyyy-MM-dd HH:mm:ss"), Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.menu_about) { // 点击了关于菜单项
            Toast.makeText(this, "这个是表情包图库首页", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
