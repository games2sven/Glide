package com.highgreat.sven.hgglide.cache.recycle;

import android.graphics.Bitmap;

import com.highgreat.sven.hgglide.cache.Key;

public class Resource {

    private Bitmap bitmap;

    //引用计数
    /**
     * 当acquired为0的时候，回调ResourceListener
     * 将图片存入内存缓存
     */
    private int acquired;

    private Key key;

    private ResourceListener listener;

    public Resource(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public interface ResourceListener{
        void onResourceReleased(Key key, Resource resource);
    }


    public void setResourceListener(Key key,ResourceListener resourceListener){
        this.key = key;
        this.listener = resourceListener;
    }

    /**
     * 释放
     */
    public void recycle(){
        if(acquired > 0) {
            return;
        }

        if(!bitmap.isRecycled()){
            bitmap.recycle();
        }
    }

    public void acquire(){
        if(bitmap.isRecycled()){
            throw new IllegalStateException("Acquire a recycled resource");
        }
        ++acquired;
    }

    /**
     * 引用计数-1
     */
    public void release(){
        if(--acquired == 0){
            listener.onResourceReleased(key,this);
        }
    }


}
