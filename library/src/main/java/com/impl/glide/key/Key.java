package com.impl.glide.key;

import java.security.MessageDigest;

public interface Key {
    void updateDiskCacheKey(MessageDigest md);

    byte[] getKeyBytes();
}
