package com.example.emjoy.util;

import com.example.emjoy.bean.ImagesInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class crawler {

    // 地址
    public static String URL = "https://www.doutula.com/search?more=1&keyword=";
    // 获取img标签正则
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "[a-zA-z]+://img[^\\s]*";
    public static String USER_AGENT =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    public crawler(String key) {
    	URL=URL.concat(key);
    }
    
    public static void main(String[] args) {
//        try {
//            crawler cm=new crawler("熊猫人");
//            List<String> imgSrc = cm.exec();
//            //下载图片
////            imgSrc.forEach((x)->System.out.println(x));
//            for (String image:imgSrc){
//                System.out.println(image);
//            }
//        }catch (Exception e){
//            System.out.println("发生错误");
//        }

            crawler cr=new crawler("熊猫人");
            try {
                ArrayList<String> res=cr.exec();
                ArrayList<ImagesInfo> gridArray = new ArrayList<ImagesInfo>();
                for (String src:res){
                    System.out.println(src);
                    gridArray.add(new ImagesInfo(src,  null));
                }

            }catch (Exception e){
                e.printStackTrace();
            }


    }
    
   public ArrayList<String> exec() throws Exception {
	   ArrayList<String> listImageSrc=new ArrayList<String>();
	   listImageSrc.addAll(oexec("photo"));
	   listImageSrc.addAll(oexec("article"));
	   return listImageSrc;
   }
   public ArrayList<String> oexec(String type) throws Exception {
       ArrayList<String> listImageSrc=new ArrayList<String>();
       String url=URL.concat("&type="+type+"&page=");
	   for(int i=1;i<51;i++) {
		   ArrayList<String> TempList=getImageSrc(url.concat(String.valueOf(i)));
		   listImageSrc.addAll(TempList);
	   }
	   return listImageSrc;
   }
   //获取HTML内容
    private String getHtml(String urli)throws Exception {
        java.net.URL url=new URL(urli);
        HttpURLConnection Conn=(HttpURLConnection) url.openConnection();
        Conn.setRequestMethod("GET");
        Conn.setRequestProperty("User-agent", USER_AGENT);
        int code = Conn.getResponseCode();
        InputStream is;
        if (code == 200) {
        is = Conn.getInputStream(); // 得到网络返回的输入流
        } else {
        is = Conn.getErrorStream(); // 得到网络返回的输入流
        }
//        InputStreamReader isr = new InputStreamReader(Conn.getErrorStream(), "utf-8");  
        InputStreamReader isr=new InputStreamReader(is);
        BufferedReader br=new BufferedReader(isr);

        String line;
        StringBuffer sb=new StringBuffer();
        while((line=br.readLine())!=null){
//        	System.out.println(line);
            sb.append(line,0,line.length());
            sb.append('\n');
        }
        br.close();
        isr.close();
//        in.close();
        return sb.toString();
    }

    //获取ImageUrl地址
    private ArrayList<String> getImageUrl(String html){
        Matcher matcher= Pattern.compile(IMGURL_REG).matcher(html);
        ArrayList<String> listimgurl=new ArrayList<String>();
        while (matcher.find()){
            listimgurl.add(matcher.group());
        }
        return listimgurl;
    }

    //获取ImageSrc地址
    private ArrayList<String> getImageSrc(String url) throws Exception {
    	String HTML = getHtml(url);
    	ArrayList<String> imgUrl = getImageUrl(HTML);
        ArrayList<String> listImageSrc=new ArrayList<String>();
        for (String image:imgUrl){
            Matcher matcher= Pattern.compile(IMGSRC_REG).matcher(image);
            while (matcher.find()){
                listImageSrc.add(matcher.group().substring(0, matcher.group().length()-1));
            }
        }
        return listImageSrc;
    }

    //下载图片
    private void Download(ArrayList<String> listImgSrc) {
        try {
            //开始时间
            Date begindate = new Date();
            for (String url : listImgSrc) {
                //开始时间
                Date begindate2 = new Date();
                String imageName = url.substring(url.lastIndexOf("/") + 1, url.length());
                java.net.URL uri = new URL(url);
                InputStream in = uri.openStream();
                FileOutputStream fo = new FileOutputStream(new File("src/res/"+imageName));
                byte[] buf = new byte[1024];
                int length = 0;
                System.out.println("开始下载:" + url);
                while ((length = in.read(buf, 0, buf.length)) != -1) {
                    fo.write(buf, 0, length);
                }
                in.close();
                fo.close();
                System.out.println(imageName + "下载完成");
                //结束时间
                Date overdate2 = new Date();
                double time = overdate2.getTime() - begindate2.getTime();
                System.out.println("耗时：" + time / 1000 + "s");
            }
            Date overdate = new Date();
            double time = overdate.getTime() - begindate.getTime();
            System.out.println("总耗时：" + time / 1000 + "s");
        } catch (Exception e) {
            System.out.println("下载失败");
        }
    }
}