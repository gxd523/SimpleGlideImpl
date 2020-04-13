package com.impl.glide.fetch;

/**
 * 负责数据获取
 */
public interface DataFetcher<Data> {
    void loadData(DataFetcherCallback<? super Data> callback);

    void cancel();

    Class<?> getDataClass();

    interface DataFetcherCallback<Data> {
        /**
         * 数据加载完成
         */
        void onFetcherReady(Data data);

        /**
         * 加载失败
         */
        void onLoadFailed(Exception e);
    }
}
