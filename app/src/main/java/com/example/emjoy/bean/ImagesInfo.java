package com.example.emjoy.bean;

import android.graphics.Bitmap;

import java.util.ArrayList;

import com.example.emjoy.R;

public class ImagesInfo {
    public int pic_id;
    public String url;
    public String desc;
    public boolean bPressed;
    public int id;
    private static int seq = 0;

    public ImagesInfo(int pic_id, String desc) {
        this.pic_id = pic_id;
        this.desc = desc;
        this.bPressed = false;
        this.id = this.seq;
        this.seq++;
    }

    public ImagesInfo(String urli, String desc) {
        this.url=urli;
        this.desc = desc;
        this.bPressed = false;
        this.id = this.seq;
        this.seq++;
    }

//    private static int[] RMImageArray = {R.drawable.pic_01, R.drawable.pic_02, R.drawable.pic_03
//            , R.drawable.pic_04, R.drawable.pic_05, R.drawable.pic_06, R.drawable.pic_07
//            , R.drawable.pic_08, R.drawable.pic_09, R.drawable.pic_10, R.drawable.pic_11, R.drawable.pic_12, R.drawable.pic_13, R.drawable.pic_14};

//    public static ArrayList<ImagesInfo> getDefaultRM() {
//        ArrayList<ImagesInfo> gridArray = new ArrayList<ImagesInfo>();
//        for (int i = 0; i < RMImageArray.length; i++) {
//            gridArray.add(new ImagesInfo(RMImageArray[i],  null));
//        }
//        return gridArray;
//    }

    private static String[][] URLArray={{
            "http://img.doutula.com/production/uploads/image//2018/08/24/20180824119656_OCLDoM.gif",
            "http://img.doutula.com/production/uploads/image//2018/08/05/20180805468047_EXcaSk.jpg",
            "http://img.doutula.com/production/uploads/image//2017/02/19/20170219501000_oxKHgm.jpg",
            "http://img.doutula.com/production/uploads/image//2017/07/17/20170717232343_EXcWTV.jpg",
            "http://img.doutula.com/production/uploads/image//2018/03/14/20180314035252_HweDTP.jpg",
            "http://img.doutula.com/production/uploads/image//2019/08/13/20190813652227_qIKRMS.gif",
            "http://img.doutula.com/production/uploads/image//2018/04/15/20180415793523_HCPvfQ.jpg",
            "http://img.doutula.com/production/uploads/image//2017/12/13/20171213171079_FXBpZK.jpg",
            "http://img.doutula.com/production/uploads/image//2018/12/04/20181204878738_XjiorB.jpg",
            "http://img.doutula.com/production/uploads/image//2017/09/09/20170909970999_pDFwrE.jpg",
            "http://img.doutula.com/production/uploads/image//2018/11/28/20181128360682_mSGega.gif",
            "http://img.doutula.com/production/uploads/image//2016/06/14/20160614892407_gawGtK.jpg",
            "http://img.doutula.com/production/uploads/image//2016/06/01/20160601739202_INYvyo.jpg",
            "http://img.doutula.com/production/uploads/image//2017/11/17/20171117930469_uSTzHq.png"},{
            "http://img.doutula.com/production/uploads/image//2016/01/14/20160114730924_lSguUA.jpg",
            "http://img.doutula.com/production/uploads/image//2016/02/17/20160217687486_uSDOdQ.jpg",
            "http://img.doutula.com/production/uploads/image//2016/01/18/20160118089005_ZqpyWm.jpg",
            "http://img.doutula.com/production/uploads/image//2016/12/25/20161225669470_sYiWHm.jpg",
            "http://img.doutula.com/production/uploads/image//2016/01/17/20160117011666_eGOnNZ.jpg",
            "http://img.doutula.com/production/uploads/image//2016/01/15/20160115831604_mltyGW.jpg",
            "http://img.doutula.com/production/uploads/image//2016/04/22/20160422284525_guEHkA.jpg",
            "http://img.doutula.com/production/uploads/image//2017/07/20/20170720514813_XjOGYf.jpg",
            "http://img.doutula.com/production/uploads/image//2017/02/12/20170212907618_onkMwF.jpg",
            "http://img.doutula.com/production/uploads/image//2017/01/17/20170117629321_DQYrJF.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8rvr8lh5sj20630603ym.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8okh5av2tj20c8096q44.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8okh13a10j20c80au0tn.jpg",
            "http://ww1.sinaimg.cn/bmiddle/9150e4e5ly1fw02123ewlj205904jdfn.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8okh3v60dj20c809675h.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8okgyboodj20c80agmy4.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8po2lgo2uj20c20bigm7.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8rvr4h3p6j2063060t8p.jpg",
            "http://ww1.sinaimg.cn/bmiddle/9150e4e5jw1fc3q2d6sjuj208c08cjs6.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8pm980l24j205k05kq32.jpg"},
            {
            "http://img.doutula.com/production/uploads/image//2016/02/10/20160210076206_swfVBc.jpg",
            "http://img.doutula.com/production/uploads/image//2016/01/30/20160130120706_BrzaOI.jpg",
            "http://img.doutula.com/production/uploads/image//2016/01/06/20160106058686_pwOcqx.jpg",
            "http://img.doutula.com/production/uploads/image//2016/01/18/20160118100102_gknKsj.jpg",
            "http://img.doutula.com/production/uploads/image//2015/12/31/20151231530908_HKtwUn.jpg",
            "http://ww1.sinaimg.cn/bmiddle/9150e4e5ly1fr5i4k8rxbj20fj0alaag.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8t5pt7fdbj205405imwy.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8oif5g596j20c80c7t9t.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8oie9nuz9j20c80c775j.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8ofqocsoaj205k05xaa0.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8oie8af76j20c80c7gmt.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8oiecr79mj20c80c775e.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8p6t96vgcj20jg0jgwgu.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8ofribb7oj204f04hzk3.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8pv8rcemqj20c80c8glv.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8pv8q09f5j20c80c8aae.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8pv8jo8u2j20c80c80t0.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8p3irizp1j20c80c8my3.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8pv9iffyrj20c80c8q39.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8p3iyd97kj20c80c8mxz.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8ofrlf556j206708c748.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8pq8s0q88j209q08xmxw.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8p3ipwkdwj20c80br74z.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8p3inanflj20c80c50t0.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8pq9mpxzvj20b409lt9g.jpg",
            "http://ww1.sinaimg.cn/bmiddle/6af89bc8gw1f8pq9pjz5rj209q083gmq.jpg"},
            {
            "http://img.doutula.com/production/uploads/image//2018/08/24/20180824119656_OCLDoM.gif",
            "http://img.doutula.com/production/uploads/image//2018/08/05/20180805468047_EXcaSk.jpg",
            "http://img.doutula.com/production/uploads/image//2017/02/19/20170219501000_oxKHgm.jpg",
            "http://img.doutula.com/production/uploads/image//2017/07/17/20170717232343_EXcWTV.jpg",
            "http://img.doutula.com/production/uploads/image//2018/03/14/20180314035252_HweDTP.jpg",
            "http://img.doutula.com/production/uploads/image//2019/08/13/20190813652227_qIKRMS.gif",
            "http://img.doutula.com/production/uploads/image//2018/04/15/20180415793523_HCPvfQ.jpg",
            "http://img.doutula.com/production/uploads/image//2017/12/13/20171213171079_FXBpZK.jpg",
            "http://img.doutula.com/production/uploads/image//2018/12/04/20181204878738_XjiorB.jpg",
            "http://img.doutula.com/production/uploads/image//2017/09/09/20170909970999_pDFwrE.jpg",
            "http://img.doutula.com/production/uploads/image//2018/11/28/20181128360682_mSGega.gif",
            "http://img.doutula.com/production/uploads/image//2016/06/14/20160614892407_gawGtK.jpg",
            "http://img.doutula.com/production/uploads/image//2016/06/01/20160601739202_INYvyo.jpg",
            "http://img.doutula.com/production/uploads/image//2017/11/17/20171117930469_uSTzHq.png"},
    };

    public static ArrayList<ImagesInfo> getDefault(int index) {
        ArrayList<ImagesInfo> gridArray = new ArrayList<ImagesInfo>();
        for (int i = 0; i < URLArray[index].length; i++) {
            gridArray.add(new ImagesInfo(URLArray[index][i],  null));
        }
        return gridArray;
    }

}

