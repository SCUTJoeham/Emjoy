package com.example.emjoy;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageAlphaBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageCrosshatchFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageOverlayBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToonFilter;

public class Filter_Activity extends AppCompatActivity {
    private Bundle bundle;
    public static final int PHOTO_CHOOSE=1;
    public static final int Filter=3;

    private ImageView imageView;
    private Button btn_save;
    private GridLayout gridLayout;

    private int columnCount; //列数
    private int screenWidth; //屏幕宽度
    private String pic_path;

    private GPUImage gpuImage;

    private float mHue = 0, mSaturation = 1f, mLum = 1f;
    private static final int MID_VALUE = 128;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 设置空标题
        tl_head.setTitle("");
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);

        bundle = this.getIntent().getExtras();
        int trans_id=bundle.getInt("id");

        final SeekBar seek1=findViewById(R.id.seek1);
        final SeekBar seek2=findViewById(R.id.seek2);
        final SeekBar seek3=findViewById(R.id.seek3);
        final SeekBar seek4=findViewById(R.id.seek4);
        final SeekBar seek5=findViewById(R.id.seek5);
        final SeekBar seek6=findViewById(R.id.seek6);
        final SeekBar seek7=findViewById(R.id.seek7);

        gridLayout = findViewById(R.id.gridLayout);
        columnCount = gridLayout.getColumnCount();
        screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Button button = (Button) gridLayout.getChildAt(i);
            button.setWidth(screenWidth / columnCount);
        }

        imageView=(ImageView)findViewById(R.id.imageView2);
        btn_save=(Button)findViewById(R.id.btn_save);
        gpuImage = new GPUImage(this);
        if(trans_id==Filter){
            openAlbum();
        }

        //亮度
        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Bitmap mBitmap= BitmapFactory.decodeFile(pic_path);
//                Drawable drawable=imageView.getDrawable();
//                Bitmap mBitmap=getBitmap(drawable);
                mLum = progress * 1f / MID_VALUE;
                Bitmap bitmap = ImageHelper.getChangedBitmap(mBitmap, mHue, mSaturation, mLum);
                imageView.setImageBitmap(bitmap);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //饱和度
        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Bitmap mBitmap= BitmapFactory.decodeFile(pic_path);
//                Drawable drawable=imageView.getDrawable();
//                Bitmap mBitmap=getBitmap(drawable);
                mSaturation = progress * 1f / MID_VALUE;
                Bitmap bitmap = ImageHelper.getChangedBitmap(mBitmap, mHue, mSaturation, mLum);
                imageView.setImageBitmap(bitmap);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //色调
        seek3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Bitmap mBitmap= BitmapFactory.decodeFile(pic_path);
//                Drawable drawable=imageView.getDrawable();
//                Bitmap mBitmap=getBitmap(drawable);
                mHue = (progress - MID_VALUE) * 1f / MID_VALUE * 180;
                Bitmap bitmap = ImageHelper.getChangedBitmap(mBitmap, mHue, mSaturation, mLum);
                imageView.setImageBitmap(bitmap);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //对比度
        seek4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Bitmap bitmap= BitmapFactory.decodeFile(pic_path);
//                Drawable drawable=imageView.getDrawable();
//                Bitmap bitmap=getBitmap(drawable);
                gpuImage.setImage(bitmap);
                gpuImage.setFilter(new GPUImageContrastFilter(progress*0.1f));
                bitmap = gpuImage.getBitmapWithFilterApplied();
                imageView.setImageBitmap(bitmap);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //锐度
        seek5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Bitmap bitmap= BitmapFactory.decodeFile(pic_path);
//                Drawable drawable=imageView.getDrawable();
//                Bitmap bitmap=getBitmap(drawable);
                gpuImage.setImage(bitmap);
                float value = (progress-40)*0.1f;
                gpuImage.setFilter(new GPUImageSharpenFilter(value));
                bitmap = gpuImage.getBitmapWithFilterApplied();
                imageView.setImageBitmap(bitmap);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //高斯模糊
        seek6.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Bitmap bitmap= BitmapFactory.decodeFile(pic_path);
//                Drawable drawable=imageView.getDrawable();
//                Bitmap bitmap=getBitmap(drawable);
                gpuImage.setImage(bitmap);
                float value = progress*0.1f;
                gpuImage.setFilter(new GPUImageGaussianBlurFilter(value));
                bitmap = gpuImage.getBitmapWithFilterApplied();
                imageView.setImageBitmap(bitmap);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Alpha混合
        seek7.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Bitmap bitmap= BitmapFactory.decodeFile(pic_path);
//                Drawable drawable=imageView.getDrawable();
//                Bitmap bitmap=getBitmap(drawable);
                gpuImage.setImage(bitmap);
                float value = progress*0.01f;
                gpuImage.setFilter(new GPUImageAlphaBlendFilter(value));
                bitmap = gpuImage.getBitmapWithFilterApplied();
                imageView.setImageBitmap(bitmap);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //卡通效果
        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bitmap bitmap=BitmapFactory.decodeFile(pic_path);
                Drawable drawable=imageView.getDrawable();
                Bitmap bitmap=getBitmap(drawable);
                gpuImage.setImage(bitmap);
                gpuImage.setFilter(new GPUImageToonFilter());
                bitmap = gpuImage.getBitmapWithFilterApplied();
                imageView.setImageBitmap(bitmap);
            }
        });

        //叠加混合
        findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bitmap bitmap=BitmapFactory.decodeFile(pic_path);
                Drawable drawable=imageView.getDrawable();
                Bitmap bitmap=getBitmap(drawable);
                gpuImage.setImage(bitmap);
                gpuImage.setFilter(new GPUImageOverlayBlendFilter());
                bitmap = gpuImage.getBitmapWithFilterApplied();
                imageView.setImageBitmap(bitmap);
            }
        });

        //黑白影线
        findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bitmap bitmap=BitmapFactory.decodeFile(pic_path);
                Drawable drawable=imageView.getDrawable();
                Bitmap bitmap=getBitmap(drawable);
                gpuImage.setImage(bitmap);
                gpuImage.setFilter(new GPUImageCrosshatchFilter());
                bitmap = gpuImage.getBitmapWithFilterApplied();
                imageView.setImageBitmap(bitmap);
            }
        });

        //素描效果
        findViewById(R.id.btn_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bitmap bitmap=BitmapFactory.decodeFile(pic_path);
                Drawable drawable=imageView.getDrawable();
                Bitmap bitmap=getBitmap(drawable);
                gpuImage.setImage(bitmap);
                gpuImage.setFilter(new GPUImageSketchFilter());
                bitmap = gpuImage.getBitmapWithFilterApplied();
                imageView.setImageBitmap(bitmap);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //从ImageView中获取Bitmap，再从Bitmap中导出
                Drawable drawable=imageView.getDrawable();
                Bitmap bitmap=getBitmap(drawable);
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
                    FileOutputStream out=new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                    out.flush();
                    out.close();
                    //保存图片后发送广播通知更新数据库
                    Uri uri = Uri.fromFile(file);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(Filter_Activity.this, "图片已保存至系统相册" , Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.btn_resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap= BitmapFactory.decodeFile(pic_path);
                imageView.setImageBitmap(bitmap);
                seek1.setProgress(128);
                seek2.setProgress(128);
                seek3.setProgress(128);
                seek4.setProgress(10);
                seek5.setProgress(40);
                seek6.setProgress(5);
                seek7.setProgress(50);
            }
        });
    }

    private Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
        startActivityForResult(intent,PHOTO_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case PHOTO_CHOOSE:{
                // 判断手机系统版本号
                if (Build.VERSION.SDK_INT >= 19) {
                    // 4.4及以上系统使用这个方法处理图片
                    handleImageOnKitKat(data);
                }
                else {
                    // 4.4以下系统使用这个方法处理图片
                    handleImageBeforeKitKat(data);
                }
                break;

            }
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);

        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        pic_path=imagePath;
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        }
        else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
