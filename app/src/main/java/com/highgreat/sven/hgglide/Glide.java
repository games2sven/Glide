package com.highgreat.sven.hgglide;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentCallbacks2;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.highgreat.sven.hgglide.cache.ArrayPool;
import com.highgreat.sven.hgglide.cache.DiskCache;
import com.highgreat.sven.hgglide.cache.MemoryCache;
import com.highgreat.sven.hgglide.cache.recycle.BitmapPool;
import com.highgreat.sven.hgglide.load.Engine;
import com.highgreat.sven.hgglide.load.codec.StreamBitmapDecoder;
import com.highgreat.sven.hgglide.load.model.FileLoader;
import com.highgreat.sven.hgglide.load.model.FileUriLoader;
import com.highgreat.sven.hgglide.load.model.HttpUriLoader;
import com.highgreat.sven.hgglide.load.model.StringModelLoader;
import com.highgreat.sven.hgglide.manager.RequestManagerRetriever;
import com.highgreat.sven.hgglide.request.RequestOptions;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ThreadPoolExecutor;


public class Glide implements ComponentCallbacks2 {

    private static volatile Glide glide;
    private final Context context;
    private final RequestManagerRetriever requestManagerRetriever;
    private final Engine engine;
    private final MemoryCache memoryCache;
    private final BitmapPool bitmapPool;
    private final DiskCache diskCache;
    private final ArrayPool arrayPool;
    private final ThreadPoolExecutor executor;
    private final RequestOptions defaultRequestOptions;

    private final Registry registry;

    public static RequestManager with(FragmentActivity activity){
        return getRetriever(activity).get(activity);
    }

    public static RequestManager with(Activity activity){
        return getRetriever(activity).get(activity);
    }

    public static RequestManager with(View view){
        return getRetriever(view.getContext()).get(view);
    }

    public static RequestManager with(Context context){
        return getRetriever(context).get(context);
    }

    public static RequestManager with(Fragment fragment){
        return getRetriever(fragment.getActivity()).get(fragment);
    }


    public static Glide get(Context context){
        if(glide == null){
            synchronized (Glide.class){
                if(glide == null){
                    checkAndInitializeGlide(context);
                }
            }
        }

        return glide;
    }

    Glide(Context context, RequestManagerRetriever requestManagerRetriever,GlideBuilder glideBuilder){
        this.context = context.getApplicationContext();
        this.requestManagerRetriever = requestManagerRetriever;
        this.engine = glideBuilder.engine;
        this.memoryCache = glideBuilder.memoryCache;
        this.bitmapPool = glideBuilder.bitmapPool;
        this.diskCache = glideBuilder.diskCache;
        this.arrayPool = glideBuilder.arrayPool;
        this.executor = glideBuilder.executor;
        this.defaultRequestOptions = glideBuilder.defaultRequestOptions;

        //注册机
        registry = new Registry();
        ContentResolver contentResolver = context.getContentResolver();
        registry.add(String.class, InputStream.class,new StringModelLoader.Factory())
                .add(Uri.class,InputStream.class,new HttpUriLoader.Factory())
                .add(Uri.class,InputStream.class,new FileUriLoader.Factory(contentResolver))
                .add(File.class,InputStream.class,new FileLoader.Factory())
                .register(InputStream.class,new StreamBitmapDecoder(bitmapPool,arrayPool));
    }

    public RequestManagerRetriever getRequestManagerRetriever(){
        return requestManagerRetriever;
    }

    /**
     * 获得请求管理生成器
     */
    private static RequestManagerRetriever getRetriever(Context context){
        return Glide.get(context).getRequestManagerRetriever();
    }

    public static void checkAndInitializeGlide(Context context){
        initializeGlide(context,new GlideBuilder());
    }

    private static void initializeGlide(Context context,GlideBuilder builder){
        Context application = context.getApplicationContext();
        Glide glide = builder.build(application);
        application.registerComponentCallbacks(glide);
        Glide.glide = glide;
    }

    public Engine getEngine(){
        return engine;
    }

    public Registry getRegistry(){
        return registry;
    }

    public MemoryCache getMemoryCache(){
        return memoryCache;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public BitmapPool getBitmapPool(){
        return bitmapPool;
    }

    public DiskCache getDiskCache(){
        return diskCache;
    }

    public RequestOptions getDefaultRequestOptions(){
        return defaultRequestOptions;
    }

    /**
     * 根据内存状态判断是否需要释放内存
     * @param level
     */
    @Override
    public void onTrimMemory(int level) {
        memoryCache.trimMemory(level);
        bitmapPool.trimMemory(level);
        arrayPool.trimMemory(level);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {

    }

    /**
     * 内存紧张
     */
    @Override
    public void onLowMemory() {
        //memory和bitmappool顺序不能变
        //因为memory移除后会加入复用池
        memoryCache.clearMemory();
        bitmapPool.clearMemory();
        arrayPool.clearMemory();
    }
}
