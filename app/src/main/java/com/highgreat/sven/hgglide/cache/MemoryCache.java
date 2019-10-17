package com.highgreat.sven.hgglide.cache;


import com.highgreat.sven.hgglide.cache.Key;
import com.highgreat.sven.hgglide.cache.recycle.Resource;

public interface MemoryCache {

    interface ResourceRemoveListener{
        void onResourceRemoved(Resource resource);
    }

    void setResourceRemoveListener(ResourceRemoveListener resourceRemoveListener);

    Resource put(Key key, Resource resource);

    Resource remove2(Key key);

    void clearMemory();

    void trimMemory(int level);

}
