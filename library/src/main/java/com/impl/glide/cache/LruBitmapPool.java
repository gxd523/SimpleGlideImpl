package com.impl.glide.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.impl.glide.util.BitmapUtil;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by guoxiaodong on 2020/3/15 17:12
 */
public class LruBitmapPool extends LruCache<Integer, Bitmap> {
    private NavigableMap<Integer, Object> map = new TreeMap<>();
    private boolean isRemoving;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return BitmapUtil.getBitmapSize(value);
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        map.remove(key);
        if (!isRemoving) {
            oldValue.recycle();
        }
    }

    public void put(Bitmap bitmap) {
        if (!bitmap.isMutable()) {// 不支持复用就回收掉
            bitmap.recycle();
            return;
        }

        int bitmapSize = BitmapUtil.getBitmapSize(bitmap);
        if (bitmapSize >= maxSize()) {
            bitmap.recycle();
            return;
        }

        put(bitmapSize, bitmap);
        map.put(bitmapSize, null);
    }

    public Bitmap get(int bitmapSize) {
        // 获取一个最接近key值的key
        Integer key = map.ceilingKey(bitmapSize);
        if (key != null && key <= bitmapSize * 2) {// 复用的bitmap不能超过需求的两倍，不然浪费了
            isRemoving = true;
            Bitmap bitmap = remove(key);
            isRemoving = false;
            return bitmap;
        }
        return null;
    }
}
