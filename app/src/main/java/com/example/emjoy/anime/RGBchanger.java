package com.example.emjoy.anime;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Log;

public class RGBchanger {
    private static final String TAG = "RGBchanger";
    //使用otsu计算出的阈值进行二值化
    public static Bitmap convertToBI_otsu(Bitmap bmp){
        int alpha = 0xFF << 24;
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int[] pixels=new int[width*height];
        int threshold=Otsu.otsu(bmp);
        bmp.getPixels(pixels,0,width,0,0,width,height);//获取bmp的位图数组
        pixels=convertToGrey(pixels);//转换为灰度图
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                int p=y*width+x;
                int grey=pixels[p];
                grey=grey&0x000000FF;//灰度值
                if(grey<threshold){
                    pixels[p]=alpha|0;
                }else{
                    pixels[p]=alpha|0xFFFFFF;
                }
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
        return resizeBmp;
    }
    //使用wellner算法
    public static Bitmap convertToBI(Bitmap bmp, int radius, int threshold) {
        Log.d(TAG, "convertToBI: start");
        int alpha = 0xFF << 24;
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int sum,invertThreshold;
        int[] pixels=new int[width*height];
        bmp.getPixels(pixels,0,width,0,0,width,height);//获取bmp的位图数组
        pixels=convertToGrey(pixels);//转换为灰度图
        invertThreshold=100-threshold;//这是计算平均值的比例

        //开始二值化
        for(int y=0;y<height;y++){
            int row=y*width;
            sum=(pixels[row]&0x000000FF)*radius;
            for(int x=0;x<width;x++){
                int xx=x+radius;
                if(xx>=width){
                    xx=width-1;
                }
                int grey1=(pixels[row+x] & 0x000000FF);
                int grey2=(pixels[row+xx] & 0x000000FF);

                sum+=(grey1-grey2);
                if(grey1*100*radius>sum*invertThreshold){
                    pixels[row+x]=alpha|0xFFFFFF;
                }else{
                    pixels[row+x]=alpha|0;
                }
            }
        }
        // 新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
        return resizeBmp;
    }
    //使用自定义阈值
    public static Bitmap simpleBI(Bitmap bmp, int threshold){
        int alpha = 0xFF << 24;
        int width=bmp.getWidth();
        int height=bmp.getHeight();
        int[] pixels=new int[width*height];
        bmp.getPixels(pixels,0,width,0,0,width,height);
        pixels=convertToGrey(pixels);
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                int p=y*width+x;
                int grey=pixels[p]&0x000000FF;
                if(grey<threshold){
                    pixels[p]=alpha|0;
                }else{
                    pixels[p]=alpha|0xFFFFFF;
                }
            }
        }
        // 新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
        return resizeBmp;
    }
    public static int[] convertToGrey(int[] pixels){
        int[] returnArr=new int[pixels.length];
        for(int i=0;i<pixels.length;i++){
            int alpha = 0xFF << 24;
            int grey=pixels[i];
            int red = ((grey & 0x00FF0000) >> 16);
            int green = ((grey & 0x0000FF00) >> 8);
            int blue = (grey & 0x000000FF);

            int avg=(int) ( red * 0.3 + green * 0.59 + blue * 0.11);
            returnArr[i]=alpha | (avg << 16) | (avg << 8) | avg;
        }
        return returnArr;
    }
}
