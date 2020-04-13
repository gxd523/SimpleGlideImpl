package com.impl.glide.load;

import android.net.Uri;

import com.impl.glide.fetch.HttpFetcher;
import com.impl.glide.key.ObjectKey;

import java.io.InputStream;

public class HttpLoader implements ModelLoader<Uri, InputStream> {
    private HttpLoader() {
    }

    @Override
    public boolean handles(Uri uri) {
        String scheme = uri.getScheme();
        return scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"));
    }

    @Override
    public LoadData<InputStream> buildData(Uri uri) {
        return new LoadData<>(new ObjectKey(uri), new HttpFetcher(uri));
    }

    public static class Factory implements ModelLoaderFactory<Uri, InputStream> {
        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry registry) {
            return new HttpLoader();
        }
    }
}
