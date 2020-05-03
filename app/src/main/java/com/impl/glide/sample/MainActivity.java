package com.impl.glide.sample;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.impl.glide.cache.ActiveResource;
import com.impl.glide.cache.LruBitmapPool;
import com.impl.glide.cache.LruMemoryCache;
import com.impl.glide.fetch.DataFetcher;
import com.impl.glide.key.Key;
import com.impl.glide.key.ObjectKey;
import com.impl.glide.load.FileUriLoader;
import com.impl.glide.load.HttpLoader;
import com.impl.glide.load.ModelLoader;
import com.impl.glide.load.ModelLoaderRegistry;
import com.impl.glide.load.StringModelLoader;
import com.impl.glide.recycle.Resource;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Key key = new ObjectKey(new Object());
        Resource resource = getResource(key);
        if (resource != null) {
            Log.d("gxd", "get resource!");
        }

        testLoader();
    }

    private void testLoader() {
        ModelLoaderRegistry modelLoaderRegistry = new ModelLoaderRegistry();
        modelLoaderRegistry.add(String.class, InputStream.class, new StringModelLoader.Factory());
        modelLoaderRegistry.add(Uri.class, InputStream.class, new HttpLoader.Factory());
        modelLoaderRegistry.add(Uri.class, InputStream.class, new FileUriLoader.Factory(getContentResolver()));

        ModelLoader<String, InputStream> modelLoader = modelLoaderRegistry.build(String.class, InputStream.class);
        String imageUrl = "https://dss0.bdstatic.com/-0U0bnSm1A5BphGlnYG/tam-ogel/920152b13571a9a38f7f3c98ec5a6b3f_122_122.jpg";
        DataFetcher<InputStream> fetcher = modelLoader.buildData(imageUrl).fetcher;
        callInThread(() -> fetcher.loadData(new DataFetcher.DataFetcherCallback<InputStream>() {
            @Override
            public void onFetcherReady(InputStream inputStream) {

            }

            @Override
            public void onLoadFailed(Exception e) {

            }
        }));
    }

    private void callInThread(Runnable runnable) {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        executorService.execute(runnable);
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
