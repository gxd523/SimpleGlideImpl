package com.impl.glide.cache.disk;

import com.impl.glide.key.Key;

import java.io.File;

public interface DiskCache {
    File get(Key key);

    void put(Key key, Writer writer);

    void delete(Key key);

    void clear();

    interface Writer {
        boolean write(File file);
    }
}
