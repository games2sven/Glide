package com.highgreat.sven.hgglide.load;

import android.support.annotation.Nullable;

import com.highgreat.sven.hgglide.cache.Key;

import java.security.MessageDigest;


public class Objectkey implements Key {

    private final Object object;
    public Objectkey(Object object){
        this.object = object;
    }

    /**
     * MessageDigest 可以用此类可以对数据加密。加密的方式可以是MD5 或 SHA
     * @param md
     */
    @Override
    public void updateDiskCacheKey(MessageDigest md) {
        md.update(getKeyBytes());
    }

    @Override
    public byte[] getKeyBytes() {
        return object.toString().getBytes();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Objectkey objectkey = (Objectkey) obj;
        return object != null ? object.equals(objectkey.object):objectkey.object == null;
    }

    @Override
    public int hashCode() {
        return object != null ? object.hashCode() : 0;
    }
}
