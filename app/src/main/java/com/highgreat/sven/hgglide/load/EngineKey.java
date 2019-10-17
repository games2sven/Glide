package com.highgreat.sven.hgglide.load;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.highgreat.sven.hgglide.cache.Key;

import java.security.MessageDigest;


public class EngineKey implements Key {
    private final Object model;
    private final int width;
    private final int height;
    private int hashCode;

    public EngineKey(Object model,int width,int height){
        this.model = model;
        this.width = width;
        this.height = height;
    }


    @Override
    public void updateDiskCacheKey(MessageDigest md) {
        md.update(getKeyBytes());
    }

    @Override
    public byte[] getKeyBytes() {
        return toString().getBytes();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass())return false;
        EngineKey engineKey = (EngineKey) obj;

        if (width != engineKey.width) return false;
        if (height != engineKey.height) return false;
        if (hashCode != engineKey.hashCode) return false;
        return model != null ? model.equals(engineKey.model) : engineKey.model == null;
    }

    @NonNull
    @Override
    public String toString() {
        return "EngineKey{" +
                "model=" + model +
                ", width=" + width +
                ", height=" + height +
                ", hashCode=" + hashCode +
                '}';
    }
}
