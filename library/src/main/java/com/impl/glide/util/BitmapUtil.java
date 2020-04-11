package com.impl.glide.util;

import android.graphics.Bitmap;
import android.os.Build;

/**
 * Created by guoxiaodong on 2020/3/15 18:22
 */
public class BitmapUtil {
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }
}
