package com.example.emjoy.crop.util;

import android.content.Context;
import android.support.annotation.Nullable;


public class UIUtil {
    public static int dip2px(@Nullable Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}