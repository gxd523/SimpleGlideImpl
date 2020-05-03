package com.impl.glide.load;


import com.impl.glide.key.Key;
import com.impl.glide.fetch.DataFetcher;

/**
 * @param <Model> 表示的是数据的来源(Uri/String...)
 * @param <Data>  加载成功后的返回数据类型(inputStream, byte[])
 */
public interface ModelLoader<Model, Data> {
    interface ModelLoaderFactory<Model, Data> {
        ModelLoader<Model, Data> build(ModelLoaderRegistry registry);
    }

    class LoadData<Data> {
        /**
         * 缓存的key
         */
        public final Key key;
        /**
         * 加载数据
         */
        public final DataFetcher<Data> fetcher;

        LoadData(Key key, DataFetcher<Data> fetcher) {
            this.key = key;
            this.fetcher = fetcher;
        }
    }

    /**
     * 检查此Loader能否处理对应Model的数据
     */
    boolean handles(Model model);

    /**
     * 创建加载的数据方式
     */
    LoadData<Data> buildData(Model model);
}