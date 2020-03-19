package com.example.emjoy.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.emjoy.Crop_Activity;
import com.example.emjoy.R;
import com.example.emjoy.bean.ImagesInfo;
import com.example.emjoy.widget.RecyclerExtras.OnItemClickListener;
import com.example.emjoy.widget.RecyclerExtras.OnItemLongClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@SuppressLint("DefaultLocale")
public class RecyclerGridAdapter extends RecyclerView.Adapter<ViewHolder> implements
        OnItemClickListener, OnItemLongClickListener {
    private final static String TAG = "RecyclerGridAdapter";
    private Context mContext; // 声明一个上下文对象
    private ArrayList<ImagesInfo> mImagesArray;

    public RecyclerGridAdapter(Context context, ArrayList<ImagesInfo> ImagesArray) {
        mContext = context;
        mImagesArray = ImagesArray;
    }

    // 获取列表项的个数
    public int getItemCount() {
        return mImagesArray.size();
    }

    // 创建列表项的视图持有者
    public ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        // 根据布局文件item_grid.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_grid, vg, false);
        return new ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    public void onBindViewHolder(ViewHolder vh, final int position) {
        ItemHolder holder = (ItemHolder) vh;
//        holder.iv_pic.setImageResource(mImagesArray.get(position).pic_id);
//        holder.iv_pic.setImageURL("https://g.csdnimg.cn/static/user-reg-year/1x/6.png");
        Glide.with(mContext).load(mImagesArray.get(position).url).into(holder.iv_pic);
        // 列表项的点击事件需要自己实现
        holder.ll_item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });
        // 列表项的长按事件需要自己实现
        holder.ll_item.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(v, position);
                }
                return true;
            }
        });
    }

    // 获取列表项的类型
    public int getItemViewType(int position) {
        return 0;
    }

    // 获取列表项的编号
    public long getItemId(int position) {
        return position;
    }

    // 定义列表项的视图持有者
    public class ItemHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_item; // 声明列表项的线性布局
        public ImageView iv_pic;
//        public imageView_src iv_pic; // 声明列表项图标的图像视图
        public TextView tv_title; // 声明列表项标题的文本视图

        public ItemHolder(View v) {
            super(v);
            ll_item = v.findViewById(R.id.ll_item);
            iv_pic = v.findViewById(R.id.iv_pic);
        }
    }

    // 声明列表项的点击监听器对象
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    // 声明列表项的长按监听器对象
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    // 处理列表项的点击事件
    public void onItemClick(View view, final int position) {
//        String desc = String.format("您点击了第%d项", position + 1);
//        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
        final Dialog dialog=new Dialog(mContext, R.style.AppTheme);
        ImageView iv=new ImageView(mContext);
        //宽高
        iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //设置Padding
        iv.setPadding(20,20,20,20);
        //imageView设置图片
        Glide.with(mContext).load(mImagesArray.get(position).url).into(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //弹出的“保存图片”的Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(new String[]{"保存图片","收藏图片"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            String state= Environment.getExternalStorageState();
                            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                                return;
                            }

                            File dir=new File("/storage/emulated/0/picture/");
                            if(!dir.exists()){
                                dir.mkdir();
                            }
                            String filename= UUID.randomUUID().toString()+".jpg";
                            File file = new File(dir, filename);
                            try {
                                Bitmap bitmap=Glide.with(mContext).asBitmap().load(mImagesArray.get(position).url).submit().get();
                                FileOutputStream out=new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                                out.flush();
                                out.close();
                                //保存图片后发送广播通知更新数据库
                                Uri uri = Uri.fromFile(file);
                                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            Toast.makeText(mContext, "图片已保存至系统相册" , Toast.LENGTH_LONG).show();
//                            try {
//                                saveCroppedImage(Glide.with(mContext).asBitmap().load(mImagesArray.get(position).url).submit().get());
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
                       }
                        else
                            Toast.makeText(mContext, "图片已收藏！", Toast.LENGTH_SHORT).show();

                    }
                });
                builder.show();
                return true;
            }
        });
        dialog.setContentView(iv);

        dialog.show();
    }

    // 处理列表项的长按事件
    public void onItemLongClick(View view, int position) {
//        String desc = String.format("您长按了第%d项", position + 1);
//        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }

    //保存图片
    private void saveCroppedImage(Bitmap bmp) {
        File file = new File("/sdcard/myFolder");
        if (!file.exists())
            file.mkdir();

        file = new File("/sdcard/temp.jpg".trim());
        String fileName = file.getName();
        String mName = fileName.substring(0, fileName.lastIndexOf("."));
        String sName = fileName.substring(fileName.lastIndexOf("."));

        // /sdcard/myFolder/temp_cropped.jpg
        String newFilePath = "/sdcard/myFolder" + "/" + mName + "_cropped" + sName;
        file = new File(newFilePath);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
