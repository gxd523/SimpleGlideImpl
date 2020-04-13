package com.impl.glide.load;

import android.content.ContentResolver;
import android.net.Uri;

import com.impl.glide.fetch.FileUriFetcher;
import com.impl.glide.key.ObjectKey;

import java.io.InputStream;

public class FileUriLoader implements ModelLoader<Uri, InputStream> {
    private final ContentResolver contentResolver;

    private FileUriLoader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public boolean handles(Uri uri) {
        return ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme());
    }

    @Override
    public LoadData<InputStream> buildData(Uri uri) {
        return new LoadData<>(new ObjectKey(uri), new FileUriFetcher(uri, contentResolver));
    }

    public static class Factory implements ModelLoaderFactory<Uri, InputStream> {
        private final ContentResolver contentResolver;

        public Factory(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry registry) {
            return new FileUriLoader(contentResolver);
        }
    }
}
