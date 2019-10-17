package com.highgreat.sven.hgglide.load;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.highgreat.sven.hgglide.Glide;
import com.highgreat.sven.hgglide.cache.Key;
import com.highgreat.sven.hgglide.cache.recycle.Resource;
import com.highgreat.sven.hgglide.request.ResourceCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class EngineJob implements DecodeJob.Callback{

    private static final String TAG = "EngineJob";

    private final List<ResourceCallback> cbs = new ArrayList<>();

    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper(),new MainThreadCallback());

    private boolean isCancelled;
    private DecodeJob decodeJob;
    private final EngineJobListener listener;
    private EngineKey key;
    private final ThreadPoolExecutor executor;
    private Resource resource;

    private static final int MSG_COMPLETE = 1;
    private static final int MSG_EXCEPTION = 2;
    private static final int MSG_CANCELLED = 3;

    public EngineJob(Context context, EngineKey key, EngineJobListener listener){
        Glide glide  = Glide.get(context);
        this.key = key;
        this.listener = listener;
        this.executor = glide.getExecutor();
    }

    public void start(DecodeJob decodeJob){
        this.decodeJob = decodeJob;
        executor.execute(decodeJob);
    }

    @Override
    public void onResourceReady(Resource resource) {
        Log.e(TAG, "加载任务成功回调");
        this.resource = resource;
        MAIN_THREAD_HANDLER.obtainMessage(MSG_COMPLETE,this).sendToTarget();
    }

    @Override
    public void onLoadFailed(Throwable e) {
        MAIN_THREAD_HANDLER.obtainMessage(MSG_EXCEPTION,this).sendToTarget();
    }


    /**
     * 任务工作回调
     */
    public interface EngineJobListener{
        void onEngineJobComplete(EngineJob engineJob, Key key, Resource resource);

        void onEngineJobCancelled(EngineJob engineJob, Key key);
    }

    public void addCallback(ResourceCallback cb){
        Log.e(TAG, "设置加载状态监听");
        cbs.add(cb);
    }

    public void removeCallback(ResourceCallback cb){
        cbs.remove(cb);
        //这一个请求取消了，可能还有其他地方的请求
        //只有回调为空 才表示请求需要取消
        if(cbs.isEmpty()){
            cancel();
        }
    }

    private static class MainThreadCallback implements Handler.Callback{

        @Override
        public boolean handleMessage(Message msg) {
            EngineJob job = (EngineJob) msg.obj;
            switch (msg.what){
                case MSG_COMPLETE:
                    job.handleResultOnMainThread();
                    break;
                case MSG_EXCEPTION:
                    job.handleExceptionOnMainThread();
                    break;
                case MSG_CANCELLED:
                    job.handleCancelledOnMainThread();
                    break;
                default:
                    throw new IllegalStateException("Unrecognized message: " + msg.what);
            }
            return true;
        }
    }

    private void handleResultOnMainThread(){
        Log.e(TAG, "成功回调到主线程");
        if(isCancelled){
            resource.recycle();
            release();
            return;
        }
        resource.acquire();
        listener.onEngineJobComplete(this,key,resource);
        for (int i = 0, size = cbs.size(); i < size; i++) {
            ResourceCallback cb = cbs.get(i);
            resource.acquire();
            cb.onResourceReady(resource);
        }
        resource.release();
        release();
    }

    private void handleExceptionOnMainThread(){
        if(isCancelled){
            release();
            return;
        }
        listener.onEngineJobComplete(this,key,null);
        for (ResourceCallback cb : cbs) {
            cb.onResourceReady(null);
        }
    }

    private void handleCancelledOnMainThread(){
        listener.onEngineJobCancelled(this,key);
        release();
    }

    void cancel(){
        isCancelled = true;
        decodeJob.cancel();
        listener.onEngineJobCancelled(this,key);
    }

    private void release(){
        cbs.clear();
        key = null;
        resource = null;
        isCancelled = false;
        decodeJob = null;
    }

}
