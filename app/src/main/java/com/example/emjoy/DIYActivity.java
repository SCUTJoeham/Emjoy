package com.example.emjoy;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;


public class DIYActivity extends AppCompatActivity {
    public static final int Filter=3;
    public static final int Crop=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy);

        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = findViewById(R.id.tl_head);
        // 设置空标题
        tl_head.setTitle("");
        // 使用tl_head替换系统自带的ActionBar
        setSupportActionBar(tl_head);

        //样式设置
        setDrawable(R.drawable.gif,R.id.gif_button);
        setDrawable(R.drawable.cut,R.id.crop_button);
        setDrawable(R.drawable.anime,R.id.anime_button);
        setDrawable(R.drawable.filter,R.id.filter_button);
        setDrawable(R.drawable.paint,R.id.doodle_button);


        Button gif_button=(Button)findViewById(R.id.gif_button);
        Button filter_button=(Button)findViewById(R.id.filter_button) ;
        Button crop_button=(Button)findViewById(R.id.crop_button);

        //获取动态权限
        if (ContextCompat.checkSelfPermission(DIYActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DIYActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0x1);}

        gif_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_gif = new Intent(DIYActivity.this,Gif_maker_Activity.class);
                DIYActivity.this.startActivity(intent_gif);
            }
        });

        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DIYActivity.this,Filter_Activity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("id",Filter);
                intent.putExtras(bundle);
                DIYActivity.this.startActivity(intent);
            }
        });

        crop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DIYActivity.this,Crop_Activity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("id",Crop);
                intent.putExtras(bundle);
                DIYActivity.this.startActivity(intent);
            }
        });

        findViewById(R.id.doodle_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DIYActivity.this,Doodle_Activity.class);
               DIYActivity.this.startActivity(intent);
            }
        });
        findViewById(R.id.anime_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DIYActivity.this,Anime_Acivity.class);
                DIYActivity.this.startActivity(intent);
            }
        });
    }


    public void setDrawable(int Did,int Bid){
        Button btn=findViewById(Bid);
        Drawable d=getResources().getDrawable(Did);
        d.setBounds(0,60,160,220);
        btn.setCompoundDrawables(null,d,null,null);
    }

}
