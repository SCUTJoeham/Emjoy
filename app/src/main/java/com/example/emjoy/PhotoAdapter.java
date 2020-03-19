package com.example.emjoy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class PhotoAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;

    public PhotoAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<String> getList() {
        return list;
    }

    @Override
    public int getCount() {
        return list == null ? 1 : list.size() + 1;
    }

    @Override
    public String getItem(int position) {
        if (list == null || position < 0 || position >= list.size()) return null;
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.photo_adapter_item, null);
            holder = new ViewHolder();
            holder.image_view = (ImageView) convertView.findViewById(R.id.image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list == null || position >= list.size()) {
            Glide.with(context).load(R.drawable.icon_plus).into(holder.image_view);
            holder.image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof Gif_maker_Activity) ((Gif_maker_Activity) context).photoPick();
                }
            });
        } else {
            //加载本地uri资源
            Log.d("photoAdapter", "getView: ---->" + getItem(position));
            Glide.with(context).load(new File(getItem(position))).into(holder.image_view);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView image_view;
    }
}
