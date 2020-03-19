package com.example.emjoy.anime;

import android.graphics.Bitmap;
import android.util.Log;

public class Otsu {//用于计算阈值
    private static final String TAG = "Otsu";
    public static int otsu(Bitmap bitmap){
        double[] hist=new double[256];
        normalizedHistogram(bitmap,hist);
        double[] omega=new double[256];//累积分布函数
        double[] avg=new double[256];//用来求灰度平均值
        omega[0]=hist[0];avg[0]=0;
        for(int i=1;i<256;i++){
            omega[i]=omega[i-1]+hist[i];//i之前的灰度值所占的比例
            avg[i]=avg[i-1]+hist[i]*i;
        }
        double mean=avg[255];//灰度的平均值
        double max=0;//最大的类间方差的值
        int g_max=0;//使类间方差最大的平均值
        for(int g=1;g<255;g++){//为什么不计算0，255的情况呢
            double PA,PB;//前景色和背景色所占比例值
            double MA,MB;//前景色与背景色的灰度平均值以及总平均值mean
            double value=0;
            PA=omega[g];
            PB=1-omega[g];
            if(PA> 0.001 && PB > 0.001) {
                MA = avg[g] / PA;
                MB = (mean - avg[g]) / PB;
                Log.d(TAG, "otsu: MA:" + MA + " MB:" + MB + " avg[" + g + "]:" + avg[g] + " PA:" + PA + " PB:" + PB);
                value = PA * (MA - mean) * (MA - mean) + PB * (MB - mean) * (MB - mean);
                if (value > max) {
                    max = value;
                    g_max = g;
                }
            }
        }
        Log.d(TAG, "otsu: max:"+max);
        return  g_max;
    }
    public static void normalizedHistogram(Bitmap bitmap, double[] hist){//计算直方图
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        int N=width*height;
        int[] pixels=new int[width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);//获取位图数组
        pixels=RGBchanger.convertToGrey(pixels);
        int a,r,g,b;
        a=255;//alpha通道
        for(int y=0;y<height;y++){//计算每个灰度值的像素个数
            for(int x=0;x<width;x++){
                int grey=pixels[y*width+x];
                b=(grey & 0x000000FF);
                ++hist[b];//没办法限定传入数组的大小，注意这里不能出错
            }
        }
        for(int i=0;i<256;i++){
            hist[i]=hist[i]/N;//计算每个灰度值的比例
        }
    }
}
