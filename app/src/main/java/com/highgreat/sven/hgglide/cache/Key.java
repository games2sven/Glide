package com.highgreat.sven.hgglide.cache;

import java.security.MessageDigest;

public interface Key {
    void updateDiskCacheKey(MessageDigest md);

    byte[] getKeyBytes();
}
