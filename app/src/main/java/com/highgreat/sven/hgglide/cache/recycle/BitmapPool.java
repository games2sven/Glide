package com.highgreat.sven.hgglide.cache.recycle;

import android.graphics.Bitmap;

/**
 * 复用池
 */
public interface BitmapPool {

    void put(Bitmap bitmap);

    Bitmap get(int width,int height,Bitmap.Config config);

    void clearMemory();

    void trimMemory(int level);

}
