package com.highgreat.sven.hgglide.load;

import android.content.Context;

import com.highgreat.sven.hgglide.Glide;
import com.highgreat.sven.hgglide.cache.ActiveResource;
import com.highgreat.sven.hgglide.cache.Key;
import com.highgreat.sven.hgglide.cache.MemoryCache;
import com.highgreat.sven.hgglide.cache.recycle.Resource;
import com.highgreat.sven.hgglide.request.ResourceCallback;

import java.util.HashMap;
import java.util.Map;

public class Engine implements Resource.ResourceListener, EngineJob.EngineJobListener, MemoryCache.ResourceRemoveListener {

    private Context context;
    ActiveResource activeResource;


    Map<Key, EngineJob> jobs = new HashMap<>();

    public Engine(Context application) {
        this.context = application;
        activeResource = new ActiveResource(this);
    }

    /**
     * 引用计数为0回调
     * 将其从正在使用集合移除 并加入内存缓存
      * @param key
     * @param resource
     */
    @Override
    public void onResourceReleased(Key key, Resource resource) {
        activeResource.deactivete(key);
        Glide.get(context).getBitmapPool().put(resource.getBitmap());
    }

    @Override
    public void onEngineJobComplete(EngineJob engineJob, Key key, Resource resource) {
        if(resource != null){
            resource.setResourceListener(key,this);
            activeResource.activete(key,resource);
        }
        jobs.remove(key);
    }

    @Override
    public void onEngineJobCancelled(EngineJob engineJob, Key key) {
        jobs.remove(key);
    }

    /**
     * 从内存缓存中移除回调
     * 将其加入复用池
     * @param resource
     */
    @Override
    public void onResourceRemoved(Resource resource) {
        Glide.get(context).getBitmapPool().put(resource.getBitmap());
    }

    public static class LoadStatus{
        private final EngineJob engineJob;
        private final ResourceCallback cb;

        LoadStatus(ResourceCallback cb,EngineJob engineJob){
            this.cb = cb;
            this.engineJob = engineJob;
        }

        public void cancel(){engineJob.removeCallback(cb);};
    }

    public LoadStatus load(Object model, int width, int height, ResourceCallback cb){
        EngineKey engineKey = new EngineKey(model, width, height);

        Resource resource = activeResource.get(engineKey);
        if(null != resource){
            //使用活动缓存数据
            resource.acquire();
            cb.onResourceReady(resource);
            return null;
        }
        //从内存缓存中移除  将它加入到活动缓存中
        resource = Glide.get(context).getMemoryCache().remove2(engineKey);
        if(null != resource){
            //使用内存缓存数据 加入到活动缓存中
            activeResource.activete(engineKey,resource);
            resource.acquire();
            resource.setResourceListener(engineKey,this);
            cb.onResourceReady(resource);
            return null;
        }

        //重复的请求 获得上一次的工作并添加监听器
        //请求完成，回调所有监听器
        EngineJob engineJob = jobs.get(engineKey);
        if(engineJob != null){
            engineJob.addCallback(cb);
            return new LoadStatus(cb,engineJob);
        }
        //创建一个新的加载任务
        engineJob = new EngineJob(context,engineKey,this);
        engineJob.addCallback(cb);
        DecodeJob decodeJob = new DecodeJob(context,model,engineKey,width,height,engineJob);
        engineJob.start(decodeJob);
        jobs.put(engineKey,engineJob);
        return new LoadStatus(cb,engineJob);
    }


}
