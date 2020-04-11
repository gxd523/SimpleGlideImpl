package com.impl.glide.cache;

import android.util.LruCache;

import com.impl.glide.key.Key;
import com.impl.glide.recycle.Resource;
import com.impl.glide.util.BitmapUtil;

/**
 * 内存缓存
 * 主动移除：图片在界面显示，因而从内存缓存中移除
 * 被动移除：超过缓存池大小被移除
 */
public class LruMemoryCache extends LruCache<Key, Resource> {
    private ResourceRemoveListener resourceRemoveListener;
    private boolean isRemoving;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public LruMemoryCache(int maxSize, ResourceRemoveListener resourceRemoveListener) {
        super(maxSize);
        this.resourceRemoveListener = resourceRemoveListener;
    }

    @Override
    protected int sizeOf(Key key, Resource value) {
        return BitmapUtil.getBitmapSize(value.getBitmap());
    }

    @Override
    protected void entryRemoved(boolean evicted, Key key, Resource oldValue, Resource newValue) {
        if (oldValue != null && !isRemoving) {
            resourceRemoveListener.onMemoryResourceRemoved(oldValue);
        }
    }

    public Resource remove2(Key key) {
        isRemoving = true;
        Resource resource = remove(key);
        isRemoving = false;
        return resource;
    }

    public interface ResourceRemoveListener {
        /**
         * 从内存缓存被动移除，要放入Bitmap复用池(主动移除是活动缓存在使用)
         */
        void onMemoryResourceRemoved(Resource resource);
    }
}
