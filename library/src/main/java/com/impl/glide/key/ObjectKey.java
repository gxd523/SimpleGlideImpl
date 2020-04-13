package com.impl.glide.key;

import java.security.MessageDigest;

public class ObjectKey implements Key {
    /**
     * 图片资源地址(网络/本地)
     */
    private final Object obj;

    public ObjectKey(Object obj) {
        this.obj = obj;
    }

    /**
     * @param messageDigest 可以用此类对数据加密(MD5/SHA)
     */
    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(getKeyBytes());
    }

    @Override
    public byte[] getKeyBytes() {
        return obj.toString().getBytes();
    }

    /**
     * 保存到ArrayList/HashMap
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectKey objectkey = (ObjectKey) o;
        return obj != null ? obj.equals(objectkey.obj) : objectkey.obj == null;
    }

    @Override
    public int hashCode() {
        return obj != null ? obj.hashCode() : 0;
    }
}
