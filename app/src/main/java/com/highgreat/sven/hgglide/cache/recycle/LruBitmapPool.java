package com.highgreat.sven.hgglide.cache.recycle;


import android.graphics.Bitmap;
import android.util.LruCache;

import java.util.NavigableMap;
import java.util.TreeMap;

//当内存缓存中bitmap被lru算法被动回收掉的时候，需要将其保存到维护了lru算法的bitmap缓存池中
public class LruBitmapPool extends LruCache<Integer ,Bitmap> implements BitmapPool {

    NavigableMap<Integer,Integer> map = new TreeMap<>();

    private final static int MAX_OVER_SIZE = 2;

    private boolean isRemoved;

    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return value.getAllocationByteCount();
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        map.remove(key);
        if(!isRemoved){
            oldValue.recycle();
        }
    }

    /**
     * 将Bitmap放入复用池
     * @param bitmap
     */
    @Override
    public void put(Bitmap bitmap) {
        //是否可复用
        if(!bitmap.isMutable()){
            bitmap.recycle();
            return ;
        }

        int size = bitmap.getAllocationByteCount();
        if(size >= maxSize()){//大于复用池的大小
            bitmap.recycle();
            return;
        }

        put(size,bitmap);
        map.put(size,0);

    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        //Bitmap 内存大小
        int size = width * height * (config == Bitmap.Config.ARGB_8888 ? 4:2);
        // 返回大于等于给定键的最小键
        Integer key = map.ceilingKey(size);
        if(null != key && key <= size * MAX_OVER_SIZE){
            isRemoved = true;
            Bitmap remove = remove(key);
            isRemoved = false;
            return remove;
        }
        return null;
    }

    @Override
    public void clearMemory() {
        evictAll();
    }

    @Override
    public void trimMemory(int level) {
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            clearMemory();
        }else if(level >= android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){
            trimToSize(maxSize() / 2);
        }
    }

}
