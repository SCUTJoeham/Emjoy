package com.example.emjoy;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.emjoy.crop.view.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Crop_Activity extends AppCompatActivity {
    private Bundle bundle;
    private String pic_path;
    public static final int PHOTO_CHOOSE=1;
    public static final int Crop=4;
    CropImageView cropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = this.getIntent().getExtras();
        setContentView(R.layout.activity_crop);

        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 设置空标题
        tl_head.setTitle("");
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);

        int trans_id=bundle.getInt("id");
        cropImageView =(CropImageView)findViewById(R.id.cropImageView);
        if (ContextCompat.checkSelfPermission(Crop_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Crop_Activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0x1);}

        switch (trans_id){
            case Crop:{
                openAlbum();
            }
        }

        findViewById(R.id.cropOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取裁剪的图片
                Bitmap cropBitMap = cropImageView.getCroppedImage();
                cropImageView.setImageBitmap(cropBitMap);
            }
        });

        //图片还原
        findViewById(R.id.reduction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapFactory.decodeFile(pic_path);
                cropImageView.setImageBitmap(bitmap);
            }
        });

        findViewById(R.id.btn_save_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable=cropImageView.getDrawable();
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
                Toast.makeText(Crop_Activity.this, "图片已保存至系统相册" , Toast.LENGTH_LONG).show();
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

    public void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setType("image/*");
        startActivityForResult(intent,PHOTO_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            default:break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void handleImageOnKitKat(Intent data){
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
        displayImage(imagePath);
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
            cropImageView.setImageBitmap(bitmap);
        }
        else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}
