package com.example.emjoy;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;


import com.example.emjoy.anime.Otsu;
import com.example.emjoy.anime.RGBchanger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Anime_Acivity extends AppCompatActivity {
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    private ImageView picture;//迷惑，为什么要用全局变量——因为要设置view
    private ImageView face;
    private Uri imageUri;
    private Bitmap image;
    private SeekBar changeThreshold;
    private static final String TAG = "Anime_Acivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime);
        face=(ImageView)findViewById(R.id.face);
        image= BitmapFactory.decodeResource(getResources(),R.drawable.qiao);
        int threshold= Otsu.otsu(image);
        Log.d(TAG, "onCreate: threshold:"+threshold);
        //拍照按钮设置
        Button takePhoto=(Button)findViewById(R.id.take_photo);
        Button chooseFromAlbum=(Button)findViewById(R.id.choose_from_album);
        changeThreshold=(SeekBar)findViewById(R.id.change_threshold);
        changeThreshold.setProgress(threshold);
        changeThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Bitmap oldBitmap=((BitmapDrawable)picture.getDrawable()).getBitmap();
                Bitmap newBitmap= RGBchanger.simpleBI(oldBitmap,progress);
                face.setImageBitmap(newBitmap);
                Log.d(TAG, String.valueOf(progress));
            }
            /*
             * 按住seekbar时的事件监听处理
             * */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(Anime_Acivity.this,"按住seekbar",Toast.LENGTH_SHORT).show();
            }
            /*
             * 放开seekbar时的时间监听处理
             * */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(Anime_Acivity.this,"放开seekbar",Toast.LENGTH_SHORT).show();
            }
        });
        picture=(ImageView)findViewById(R.id.picture);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File outputImage=new File(getCacheDir(),"output_image.jpg");
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(Anime_Acivity.this,
                            "com.example.facedetectortest.fileprovider",outputImage);//从系统获得照片
                }else{
                    imageUri= Uri.fromFile(outputImage);
                }
                Log.d("Anime_Acivity", "onClick: execute");
                //启动相机程序
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);//请求目标活动返回信息，要重写onActivityResult方法
            }
        });
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(Anime_Acivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Anime_Acivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    openAlbum();
                }
            }
        });

        //image=image.createScaledBitmap(image,1080,1920,false);
        //Bitmap faceImage=FaceHelper.genFaceBitmap(image);
        //faceImage=RGBchanger.convertToBMW(faceImage);
        picture.setImageBitmap(image);
        Bitmap imageBI=RGBchanger.convertToBI_otsu(image);
        //image=RGBchanger.convertToBI(image,8,15);
        face.setImageBitmap(imageBI);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d("Anime_Acivity", "onActivityResult: execute");
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){  //sh*t为什么request和result这么像啊！
                    try{
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    //更改对应的二值图
                    image=((BitmapDrawable)picture.getDrawable()).getBitmap();
                    int threshold=Otsu.otsu(image);
                    Bitmap imageBI=RGBchanger.simpleBI(image,threshold);
                    changeThreshold.setProgress(threshold);
                    //image=RGBchanger.convertToBI(image,8,15);
                    face.setImageBitmap(imageBI);
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);

                        //更改对应的二值图
                        image=((BitmapDrawable)picture.getDrawable()).getBitmap();
                        int threshold=Otsu.otsu(image);
                        changeThreshold.setProgress(threshold);
                        Bitmap imageBI=RGBchanger.simpleBI(image,threshold);
                        //image=RGBchanger.convertToBI(image,8,15);
                        face.setImageBitmap(imageBI);

                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //doc类型uri
            String docId= DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }
        else if("com.android.providers.downloads.documents".equalsIgnoreCase(uri.getScheme())){
            //对于content类型的操作
            imagePath=getImagePath(uri,null);
        }
        else if("file".equals(uri.getScheme())){
            //对于file类型的操作
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }
    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
    }
    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"你快同意啊！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    private String getImagePath(Uri uri, String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"There's something wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
