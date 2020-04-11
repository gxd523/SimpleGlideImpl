package com.impl.glide.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.impl.glide.cache.ActiveResource;
import com.impl.glide.cache.LruBitmapPool;
import com.impl.glide.cache.LruMemoryCache;
import com.impl.glide.key.Key;
import com.impl.glide.key.Objectkey;
import com.impl.glide.recycle.Resource;

public class MainActivity extends Activity {
    private LruBitmapPool bitmapPool = new LruBitmapPool(10);
    private LruMemoryCache memoryCache = new LruMemoryCache(10, new LruMemoryCache.ResourceRemoveListener() {
        @Override
        public void onMemoryResourceRemoved(Resource resource) {
            bitmapPool.put(resource.getBitmap());
        }
    });
    private ActiveResource activeResource = new ActiveResource(new Resource.ResourceListener() {
        @Override
        public void onActiveResourceReleased(Key key, Resource resource) {
            activeResource.deactive(key);
            memoryCache.put(key, resource);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Key key = new Objectkey(new Object());
        Resource resource = getResource(key);
        if (resource != null) {
            Log.d("gxd", "get resource!");
        }
    }

    private Resource getResource(Key key) {
        Resource resource = activeResource.get(key);
        if (resource == null) {
            resource = memoryCache.remove2(key);
            if (resource != null) {
                activeResource.active(key, resource);
            } else {
                // ...
            }
        }
        if (resource != null) {
            resource.acquire();
        }
        return resource;
    }
}
