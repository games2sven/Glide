package com.highgreat.sven.hgglide.cache;

import android.os.Build;
import android.util.LruCache;

import com.highgreat.sven.hgglide.cache.Key;
import com.highgreat.sven.hgglide.cache.recycle.Resource;

public class LruMemoryCache extends LruCache<Key, Resource> implements MemoryCache{
    private ResourceRemoveListener listener;

    private boolean isRemoved;

    public LruMemoryCache(int maxSize){
        super(maxSize);
    }

    @Override
    protected int sizeOf(Key key, Resource value) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //当在4.4以上手机复用的时候  需要通过此函数获得占用内存
            return value.getBitmap().getAllocationByteCount();
        }
        return value.getBitmap().getByteCount();
    }

    @Override
    protected void entryRemoved(boolean evicted, Key key, Resource oldValue, Resource newValue) {
        if(null != listener && null != oldValue && !isRemoved){
            this.listener.onResourceRemoved(oldValue);
        }
    }

    @Override
    public void setResourceRemoveListener(ResourceRemoveListener resourceRemoveListener) {
        this.listener = resourceRemoveListener;
    }


    //主动从内存缓存中移除
    @Override
    public Resource remove2(Key key) {
        isRemoved = true;
        Resource remove = remove(key);
        isRemoved = false;
        return remove;
    }

    @Override
    public void clearMemory() {
        evictAll();
    }

    @Override
    public void trimMemory(int level) {
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            clearMemory();
        }else if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            trimToSize(maxSize() / 2);
        }
    }
}
