package com.impl.glide.recycle;

import android.graphics.Bitmap;

import com.impl.glide.key.Key;

/**
 * Created by guoxiaodong on 2020/3/1 12:08
 */
public class Resource {
    private Bitmap bitmap;
    private ResourceListener resourceListener;
    /**
     * 引用计数
     * 为0时，回调ResourceListener.onResourceReleased()，将图片放入MemoryCache
     */
    private int acquired;
    private Key key;

    public void acquire() {
        if (bitmap.isRecycled()) {
            throw new IllegalStateException("Acquire a recycled resource");
        }
        acquired++;
    }

    public void recycle() {
        if (acquired > 0) {
            return;
        }
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public void release() {
        if (--acquired == 0) {
            if (resourceListener != null) {
                resourceListener.onActiveResourceReleased(key, this);
            }
        }
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setResourceListener(Key key, ResourceListener resourceListener) {
        this.key = key;
        this.resourceListener = resourceListener;
    }

    public interface ResourceListener {
        /**
         * 从活动缓存中移除，添加进内存缓存
         */
        void onActiveResourceReleased(Key key, Resource resource);
    }
}
