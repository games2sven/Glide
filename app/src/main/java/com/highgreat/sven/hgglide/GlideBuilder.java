package com.highgreat.sven.hgglide;

import android.app.ActivityManager;
import android.content.Context;
import android.util.DisplayMetrics;

import com.highgreat.sven.hgglide.cache.ArrayPool;
import com.highgreat.sven.hgglide.cache.DiskCache;
import com.highgreat.sven.hgglide.cache.DiskLruCacheWrapper;
import com.highgreat.sven.hgglide.cache.LruArrayPool;
import com.highgreat.sven.hgglide.cache.LruMemoryCache;
import com.highgreat.sven.hgglide.cache.MemoryCache;
import com.highgreat.sven.hgglide.cache.recycle.BitmapPool;
import com.highgreat.sven.hgglide.cache.recycle.LruBitmapPool;
import com.highgreat.sven.hgglide.load.Engine;
import com.highgreat.sven.hgglide.load.GlideExecutor;
import com.highgreat.sven.hgglide.manager.RequestManagerRetriever;
import com.highgreat.sven.hgglide.request.RequestOptions;

import java.util.concurrent.ThreadPoolExecutor;

public class GlideBuilder {

    ThreadPoolExecutor executor;
    ArrayPool arrayPool;
    BitmapPool bitmapPool;
    MemoryCache memoryCache;
    DiskCache diskCache;
    Engine engine;
    RequestOptions defaultRequestOptions = new RequestOptions();

    private static int getMaxSize(ActivityManager activityManager){
        //使用最大可用内存的0.4作为缓存使用
        final int memoryClassBytes = activityManager.getMemoryClass() * 1024 *1024;
        return Math.round(memoryClassBytes * 0.4f);
    }


    public Glide build(Context application) {
        application = application.getApplicationContext();
        if(executor == null){
            executor = GlideExecutor.newExecutor();
        }

        if(arrayPool == null){
            arrayPool = new LruArrayPool();
        }

        ActivityManager activityManager = (ActivityManager) application.getSystemService(Context
                .ACTIVITY_SERVICE);
        int maxSize = getMaxSize(activityManager);

        //减去数组缓存后的可用内存大小
        int availableSize = maxSize - arrayPool.getMaxSize();

        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        // 获得一个屏幕大小的argb所占的内存大小
        int screenSize = widthPixels * heightPixels * 4;

        //bitmap复用占 4份
        float bitmapPoolSize = screenSize * 4.0f;
        //内存缓存占 2份
        float memoryCacheSize = screenSize * 2.0f;

        if(bitmapPoolSize + memoryCacheSize <= availableSize){
            bitmapPoolSize = Math.round(bitmapPoolSize);
            memoryCacheSize = Math.round(memoryCacheSize);
        }else{
            //把总内存分成 6分
            float part = availableSize / 6.0f;
            bitmapPoolSize = Math.round(part * 4);
            memoryCacheSize = Math.round(part * 2);
        }

        if(bitmapPool == null){
            bitmapPool = new LruBitmapPool((int)bitmapPoolSize);
        }

        if(memoryCache == null){
            memoryCache = new LruMemoryCache((int)memoryCacheSize);
        }

        if(diskCache == null){
            diskCache = new DiskLruCacheWrapper(application);
        }

        if(engine == null){
            engine = new Engine(application);
        }
        memoryCache.setResourceRemoveListener(engine);

        RequestManagerRetriever requestManagerRetriever = new RequestManagerRetriever();

        return new Glide(application,requestManagerRetriever,this);
    }
}
