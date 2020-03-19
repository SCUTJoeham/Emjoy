package com.example.emjoy;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



import com.bumptech.glide.Glide;
import com.example.emjoy.gifmaker.AnimatedGifEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Gif_maker_Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView delay_text;//帧间隔的文字
    private ImageView gif_image;//生成的gif图片
    private EditText file_text;//输入框，生成的gif文件名
    private TextView generate;
    private TextView clear;

    public static final String TAG = "GifMaker";
    public static final int START_ALBUM_CODE = 0x21;

    private List<String> pics = new ArrayList<>();
    private PhotoAdapter adapter;//Adapter是数据与UI之间的桥梁
    private int delayTime;//帧间隔
    private AlertDialog alertDialog;//提示对话框

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifmaker);

        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 设置空标题
        tl_head.setTitle("");
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);

        alertDialog = new AlertDialog.Builder(this).setView(new ProgressBar(this)).setMessage("正在生成gif图片").create();
        initView();
    }

    private void initView() {
        GridView grid_view = (GridView) findViewById(R.id.grid_view);//findViewById()是找xml布局文件下的具体widget控件
        file_text = (EditText) findViewById(R.id.file_text);
        SeekBar delay_bar = (SeekBar) findViewById(R.id.delay_bar);//帧间隔拖动条
        gif_image = (ImageView) findViewById(R.id.gif_image);
        delay_text = (TextView) findViewById(R.id.delay_text);
        generate=(TextView)findViewById(R.id.generate);
        clear=(TextView)findViewById(R.id.clear);
        generate.setOnClickListener(this);
        clear.setOnClickListener(this);

        adapter = new PhotoAdapter(this, null);
        grid_view.setAdapter(adapter);

        file_text.setText("demo");//设置默认的文件名
        delayTime = delay_bar.getProgress();
        delay_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//显示帧间隔时长
                delayTime = progress;
                delay_text.setText("帧间隔时长：" + progress + "ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.generate://生成gif图
                alertDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {//创建子线程
                        String file_name = file_text.getText().toString();//获取输入框的文件名
                        createGif(TextUtils.isEmpty(file_name) ? "demo" : file_name, delayTime);

                        alertDialog.dismiss();
                    }
                }).start();
                break;
            case R.id.clear:
                clearData();
                break;
        }
    }

    private void clearData() {//清除当前的数据内容
        pics.clear();
        adapter.setList(null);
        gif_image.setImageDrawable(null);
//        reStartActivity();
    }

    private void createGif(String file_name, int delay) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//创建一个输出流
        AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
        localAnimatedGifEncoder.start(baos);//start
        localAnimatedGifEncoder.setRepeat(0);//设置生成gif的开始播放时间(0为立即开始播放)
        localAnimatedGifEncoder.setDelay(delay);

        //【注意1】开始生成gif的时候，是以第一张图片的尺寸生成gif图的大小，后面几张图片会基于第一张图片的尺寸进行裁切
        //所以要生成尺寸完全匹配的gif图的话，应先调整传入图片的尺寸，让其尺寸相同
        //【注意2】如果传入的单张图片太大的话会造成OOM，可在不损失图片清晰度先对图片进行质量压缩
        if (pics.isEmpty()) {
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeResource(getResources(), R.drawable.pic_1));
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeResource(getResources(), R.drawable.pic_2));
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeResource(getResources(), R.drawable.pic_3));
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeResource(getResources(), R.drawable.pic_4));
            localAnimatedGifEncoder.addFrame(BitmapFactory.decodeResource(getResources(), R.drawable.pic_5));
        } else {
            for (int i = 0; i < pics.size(); i++) {
                // Bitmap localBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(pics.get(i)), 512, 512);
                localAnimatedGifEncoder.addFrame(BitmapFactory.decodeFile(pics.get(i)));
            }
        }
        localAnimatedGifEncoder.finish();//finish

        //保存生成的gif图片
        File dir=new File("/storage/emulated/0/picture/");
        if(!dir.exists()){
            dir.mkdir();
        }
        final String path ="/storage/emulated/0/picture/" + file_name + ".gif";
        Log.d(TAG, "createGif: ---->" + path);

        File file = new File(dir, file_name);
        try {
            FileOutputStream fos = new FileOutputStream(path);
            baos.writeTo(fos);
            baos.flush();
            fos.flush();
            baos.close();
            fos.close();
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //更新UI
        runOnUiThread(new Runnable() {
           @Override
           public void run() {
               Glide.with(Gif_maker_Activity.this).clear(gif_image);
               Glide.with(Gif_maker_Activity.this).load(new File(path)).into(gif_image);
               Toast.makeText(Gif_maker_Activity.this, "Gif已生成。保存路径：\n" + path, Toast.LENGTH_LONG).show();
            }
       });
    }

    //打开系统图库
    public void photoPick() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, START_ALBUM_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            Uri localUri = data.getData();
            String[] arrayOfString = {"_data"};
            Cursor localCursor = getContentResolver().query(localUri, arrayOfString, null, null, null);
            localCursor.moveToFirst();
            String str = localCursor.getString(localCursor.getColumnIndex(arrayOfString[0]));
            localCursor.close();
            pics.add(str);

            Log.d(TAG, "onActivityResult: ----->" + pics.toString());
            adapter.setList(pics);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alertDialog.dismiss();
    }

    private void reStartActivity() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

}
