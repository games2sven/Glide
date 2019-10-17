package com.highgreat.sven.hgglide.cache;

import android.util.LruCache;

import java.util.NavigableMap;
import java.util.TreeMap;

public class LruArrayPool implements ArrayPool {

    public static  final int ARRAY_POOL_SIZE_BYTES = 4*1024*1024;

    private final int maxSize;
    private final NavigableMap<Integer,Integer> sortedSizes = new TreeMap<>();
    private LruCache<Integer,byte[]> cache;
    //溢出大小
    private final static int MAX_OVER_SIZE_MULTIPLE = 8;

    public LruArrayPool(){
        this(ARRAY_POOL_SIZE_BYTES);
    }

    public LruArrayPool(int maxSize){
        this.maxSize = maxSize;
        this.cache = new LruCache<Integer,byte[]>(maxSize){
            @Override
            protected int sizeOf(Integer key, byte[] value) {
                return value.length;
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, byte[] oldValue, byte[] newValue) {
                sortedSizes.remove(oldValue.length);
            }
        };
    }


    @Override
    public byte[] get(int len) {
        //获得等于或大于比len大的key
        Integer key = sortedSizes.ceilingKey(len);
        if(null != key){
            //缓存中的大小只能比需要的大小溢出8倍
            if(key <= (MAX_OVER_SIZE_MULTIPLE * len)){
                byte[] bytes = cache.remove(key);
                return bytes == null ? new byte[len] : bytes;
            }
        }

        return new byte[len];
    }

    @Override
    public void put(byte[] data) {

    }

    @Override
    public void clearMemory() {

    }

    @Override
    public void trimMemory(int level) {

    }

    @Override
    public int getMaxSize() {
        return 0;
    }
}
