package com.impl.glide.load;

import java.util.List;

public class MultiModelLoader<Model, Data> implements ModelLoader<Model, Data> {
    /**
     * FileUriModelLoader HttpUriModelLoader
     */
    private final List<ModelLoader<Model, Data>> modelLoaderList;

    MultiModelLoader(List<ModelLoader<Model, Data>> modelLoaderList) {
        this.modelLoaderList = modelLoaderList;
    }

    @Override
    public boolean handles(Model model) {
        for (ModelLoader<Model, Data> modelLoader : modelLoaderList) {
            if (modelLoader.handles(model)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LoadData<Data> buildData(Model model) {
        for (int i = 0; i < modelLoaderList.size(); i++) {
            ModelLoader<Model, Data> modelLoader = modelLoaderList.get(i);
            // Model=>Uri:http
            if (modelLoader.handles(model)) {
                return modelLoader.buildData(model);
            }
        }
        return null;
    }
}
