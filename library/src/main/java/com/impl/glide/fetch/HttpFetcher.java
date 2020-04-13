package com.impl.glide.fetch;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpFetcher implements DataFetcher<InputStream> {
    private final Uri uri;
    /**
     * 如果请求被取消
     */
    private boolean isCanceled;

    public HttpFetcher(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void loadData(DataFetcherCallback<? super InputStream> callBack) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(uri.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            inputStream = connection.getInputStream();
            int responseCode = connection.getResponseCode();
            if (isCanceled) {
                return;
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                callBack.onFetcherReady(inputStream);
            } else {
                callBack.onLoadFailed(new RuntimeException(connection.getResponseMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != connection) {
                connection.disconnect();
            }
        }
    }

    @Override
    public void cancel() {
        isCanceled = true;
    }

    @Override
    public Class<?> getDataClass() {
        return InputStream.class;
    }
}
