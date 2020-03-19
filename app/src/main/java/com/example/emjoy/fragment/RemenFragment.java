package com.example.emjoy.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.emjoy.R;
import com.example.emjoy.adapter.RecyclerGridAdapter;
import com.example.emjoy.bean.ImagesInfo;
import com.example.emjoy.util.crawler;
import com.example.emjoy.widget.SpacesItemDecoration;

public class RemenFragment extends Fragment implements OnRefreshListener{
    private static final String TAG = "RemenFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private SwipeRefreshLayout srl_1; // 声明一个下拉刷新布局对象
    private RecyclerView rv_1; // 声明一个循环视图对象
    private RecyclerGridAdapter mAdapter; // 声明一个普通网格适配器对象
    private ArrayList<ImagesInfo> mAllArray; // 热门表情队列

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        // 根据布局文件fragment生成视图对象
        mView = inflater.inflate(R.layout.fragment, container, false);
        // 从布局文件中获取名叫srl的下拉刷新布局
        srl_1 = mView.findViewById(R.id.srl);
        // 设置srl_1的下拉刷新监听器
        srl_1.setOnRefreshListener(this);
        // 设置srl_1的下拉变色资源数组
        srl_1.setColorSchemeResources(
                R.color.red, R.color.orange, R.color.green, R.color.blue);
        // 从布局文件中获取名叫rv的循环视图
        rv_1 = mView.findViewById(R.id.rv);
        // 创建一个垂直方向的普通网格布局管理器
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        // 设置循环视图的布局管理器
        rv_1.setLayoutManager(manager);
        // 获取默认的热门表情信息队列
            crawler cr=new crawler("熊猫人");

            try {
                ArrayList<String> res=cr.exec();
                ArrayList<ImagesInfo> gridArray = new ArrayList<ImagesInfo>();

                for (String src:res){
//                    gridArray.add(new ImagesInfo(src,  null));
                    Toast.makeText(mContext, src , Toast.LENGTH_LONG).show();
                }
                mAllArray.addAll(gridArray);
            }catch (Exception e){
            }
        mAllArray = ImagesInfo.getDefault(0);
        // 构建一个热门表情列表的普通网格适配器
        mAdapter = new RecyclerGridAdapter(mContext, mAllArray);
        // 设置普通网格列表的点击监听器
        mAdapter.setOnItemClickListener(mAdapter);
        // 设置普通网格列表的长按监听器
        mAdapter.setOnItemLongClickListener(mAdapter);
        // 给rv_1设置热门表情普通网格适配器
        rv_1.setAdapter(mAdapter);
        // 设置rv_1的默认动画效果
        rv_1.setItemAnimator(new DefaultItemAnimator());
        // 给rv_1添加列表项之间的空白装饰
        rv_1.addItemDecoration(new SpacesItemDecoration(3));

        return mView;
    }

    // 一旦在下拉刷新布局内部往下拉动页面，就触发下拉监听器的onRefresh方法
    public void onRefresh() {
        // 延迟若干秒后启动刷新任务
        mHandler.postDelayed(mRefresh, 2000);
    }

    private Handler mHandler = new Handler(); // 声明一个处理器对象
    // 定义一个刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            // 结束下拉刷新布局的刷新动作
            srl_1.setRefreshing(false);
            // 更新热门表情信息队列
            for (int i = mAllArray.size() - 1, count = 0; count < 5; count++) {
                ImagesInfo item = mAllArray.get(i);
                mAllArray.remove(i);
                mAllArray.add(0, item);
            }
            // 通知适配器的列表数据发生变化
            mAdapter.notifyDataSetChanged();
            // 让循环视图滚动到第一项所在的位置
            rv_1.scrollToPosition(0);
        }
    };

}
