package com.impl.glide.load;

import android.net.Uri;

import java.io.File;
import java.io.InputStream;

import androidx.annotation.NonNull;

public class FileLoader<Data> implements ModelLoader<File, Data> {
    private final ModelLoader<Uri, Data> loader;

    private FileLoader(ModelLoader<Uri, Data> loader) {
        this.loader = loader;
    }

    @Override
    public boolean handles(File file) {
        return true;
    }

    @Override
    public LoadData<Data> buildData(File file) {
        return loader.buildData(Uri.fromFile(file));
    }

    public static class Factory implements ModelLoaderFactory<File, InputStream> {
        @NonNull
        @Override
        public ModelLoader<File, InputStream> build(ModelLoaderRegistry modelLoaderRegistry) {
            return new FileLoader<>(modelLoaderRegistry.build(Uri.class, InputStream.class));
        }
    }
}
