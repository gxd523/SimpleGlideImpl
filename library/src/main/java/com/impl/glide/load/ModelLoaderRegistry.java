package com.impl.glide.load;

import java.util.ArrayList;
import java.util.List;

public class ModelLoaderRegistry {
    private List<Entry<?, ?>> entryList = new ArrayList<>();

    /**
     * 注册 Loader
     *
     * @param modelClass 数据来源类型 String File
     * @param dataClass  数据转换后类型 加载后类型 String/File->InputStream
     * @param factory    创建ModelLoader的工厂
     */
    public synchronized <Model, Data> void add(
            Class<Model> modelClass, Class<Data> dataClass,
            ModelLoader.ModelLoaderFactory<Model, Data> factory
    ) {
        entryList.add(new Entry<>(modelClass, dataClass, factory));
    }

    /**
     * 获得 对应 model与data类型的 modelloader
     */
    public <Model, Data> ModelLoader<Model, Data> build(Class<Model> modelClass, Class<Data> dataClass) {
        List<ModelLoader<Model, Data>> loaders = new ArrayList<>();
        for (Entry<?, ?> entry : entryList) {
            //是我们需要的Model与Data类型的Loader
            if (entry.handles(modelClass, dataClass)) {
                loaders.add((ModelLoader<Model, Data>) entry.factory.build(this));
            }
        }
        //找到多个匹配的loader
        if (loaders.size() > 1) {
            return new MultiModelLoader<>(loaders);
        } else if (loaders.size() == 1) {
            return loaders.get(0);
        }
        throw new RuntimeException("No Match:" + modelClass.getName() + " Data:" + dataClass.getName());
    }


    /**
     * 查找匹配的 Model类型的ModelLoader
     */
    public <Model> List<ModelLoader<Model, ?>> getModelLoaderList(Class<Model> modelClass) {
        List<ModelLoader<Model, ?>> loaders = new ArrayList<>();
        for (Entry<?, ?> entry : entryList) {
            if (entry.handles(modelClass)) {
                loaders.add((ModelLoader<Model, ?>) entry.factory.build(this));
            }
        }
        return loaders;
    }

    private static final class Entry<Model, Data> {
        Class<Model> modelClass;
        Class<Data> dataClass;
        ModelLoader.ModelLoaderFactory<Model, Data> factory;

        Entry(Class<Model> modelClass, Class<Data> dataClass, ModelLoader.ModelLoaderFactory<Model, Data> factory) {
            this.modelClass = modelClass;
            this.dataClass = dataClass;
            this.factory = factory;
        }

        boolean handles(Class<?> modelClass, Class<?> dataClass) {
            // A.isAssignableFrom(B)B和A是同一个类型或者B是A的子类
            return this.modelClass.isAssignableFrom(modelClass) && this.dataClass.isAssignableFrom(dataClass);
        }

        boolean handles(Class<?> modelClass) {
            // A.isAssignableFrom(B)B和A是同一个类型或者B是A的子类
            return this.modelClass.isAssignableFrom(modelClass);
        }
    }
}
