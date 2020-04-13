package com.impl.glide.fetch;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUriFetcher implements DataFetcher<InputStream> {
    private final Uri uri;
    private final ContentResolver contentResolver;

    public FileUriFetcher(Uri uri, ContentResolver contentResolver) {
        this.uri = uri;
        this.contentResolver = contentResolver;
    }

    @Override
    public void loadData(DataFetcherCallback<? super InputStream> callback) {
        InputStream is = null;
        try {
            is = contentResolver.openInputStream(uri);
            callback.onFetcherReady(is);
        } catch (FileNotFoundException e) {
            callback.onLoadFailed(e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void cancel() {
    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
}
