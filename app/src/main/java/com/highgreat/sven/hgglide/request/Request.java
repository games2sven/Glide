package com.highgreat.sven.hgglide.request;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.highgreat.sven.hgglide.Glide;
import com.highgreat.sven.hgglide.Target;
import com.highgreat.sven.hgglide.cache.recycle.Resource;
import com.highgreat.sven.hgglide.load.Engine;

public class Request implements Target.SizeReadyCallback,ResourceCallback{

    private Engine.LoadStatus loadStatus;
    private Object model;
    private Resource resource;
    private Drawable errorDrawable;

    @Override
    public void onSizeReady(int width, int height) {
        if(status != Status.WAITING_FOR_SIZE){
            return;
        }
        //运行状态
        status = Status.RUNNING;
        //加载图片
        Log.i("Sven","Glide 开始加载图片");
        loadStatus = Glide.get(context).getEngine().load(model,width,height,this);
    }

    @Override
    public void onResourceReady(Resource resource) {

        Log.e("Sven","request onResourceReady");
        loadStatus = null;
        this.resource = resource;
        if(resource == null){
            status = Status.FAILED;
            setErrorPlaceholder();
            return;
        }
        target.onResourceReady(resource.getBitmap());
    }

    private enum Status{
        PENDING,
        RUNNING,
        WAITING_FOR_SIZE,
        COMPLETE,
        FAILED,
        CANCELLED,
        CLEARED,
        PAUSED,
    }

    private Status status;
    private Target target;
    private Drawable placeholderDrawable;
    private Context context;
    private RequestOptions requestOptions;

    public Request(Context context,Object model,RequestOptions requestOptions,Target target){
        this.target = target;
        this.context = context;
        this.requestOptions = requestOptions;
        this.model = model;
        status = Status.PENDING;
    }

    public void begin(){
        Log.i("Sven","request begin");
        status = Status.WAITING_FOR_SIZE;
        //开始加载 先设置占位图片
        target.onLoadStarted(getPlaceholderDrawable());
        //宽高是否有效
        if(requestOptions.getOverrideWidth() > 0 && requestOptions.getOverrideHeight() > 0){
            onSizeReady(requestOptions.getOverrideWidth(),requestOptions.getOverrideHeight());
        }else{
            //否则计算size  计算完成后会回调 onSizeReady
            target.getSize(this);
        }
    }

    public void pause(){
        clear();
        status = Status.PAUSED;
    }


    public void clear(){
        if(status == Status.CLEARED){
            return ;
        }
        cancel();
        if(resource != null){
            releaseResource(resource);
        }
        status = Status.CLEARED;
    }

    public void recycle(){
        context = null;
        model = null;
        requestOptions = null;
        target = null;
        loadStatus = null;
        errorDrawable = null;
        placeholderDrawable = null;
    }


    /**
     * 取消
     * @return
     */
    public void cancel(){
        target.cancel();
        status = Status.CANCELLED;
        if(loadStatus != null){
            loadStatus.cancel();
            loadStatus = null;
        }
    }

    private void setErrorPlaceholder() {
        Drawable error = getErrorDrawable();
        if (error == null) {
            error = getPlaceholderDrawable();
        }
        target.onLoadFailed(error);
    }

    private Drawable getErrorDrawable() {
        if (errorDrawable == null && requestOptions.getErrorId() > 0) {
            errorDrawable = loadDrawable(requestOptions.getErrorId());
        }
        return errorDrawable;
    }

    private Drawable getPlaceholderDrawable(){
        if(placeholderDrawable == null && requestOptions.getPlaceholderId() > 0){
            placeholderDrawable = loadDrawable(requestOptions.getPlaceholderId());
        }
        return placeholderDrawable;
    }

    private Drawable loadDrawable(int resourceId){
        return ResourcesCompat.getDrawable(context.getResources(),resourceId,context.getTheme());
    }

    public boolean isComplete(){
        return status == Status.COMPLETE;
    }

    public boolean isCancelled(){
        return status == Status.CANCELLED || status == Status.CLEARED;
    }

    public boolean isRunning(){
        return status == Status.RUNNING || status == Status.WAITING_FOR_SIZE;
    }

    private void releaseResource(Resource resource){
        resource.release();
        this.resource = null;
    }

}
