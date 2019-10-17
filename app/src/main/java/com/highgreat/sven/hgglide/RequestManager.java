package com.highgreat.sven.hgglide;

import android.content.Context;

import com.highgreat.sven.hgglide.manager.Lifecycle;
import com.highgreat.sven.hgglide.manager.LifecycleListener;
import com.highgreat.sven.hgglide.manager.RequestTracker;
import com.highgreat.sven.hgglide.request.Request;

import java.io.File;


//生命周期绑定
public class RequestManager implements LifecycleListener,ModelTypes<RequestBuilder> {

    private final Context context;
    private final RequestTracker requestTracker;
    final Lifecycle lifecycle;

    public RequestManager(Lifecycle lifecycle, Context context){
        this.context = context;
        this.requestTracker = new RequestTracker();
        this.lifecycle = lifecycle;
        lifecycle.addListener(this);
    }

    @Override
    public void onStart() {
        requestTracker.resumeRequests();
    }

    @Override
    public void onStop() {
        requestTracker.pauseRequests();
    }

    @Override
    public void onDestroy() {
        requestTracker.clearRequests();
        lifecycle.removeListener(this);
    }

    void track(Request request){
        requestTracker.runRequest(request);
    }

    public RequestBuilder asBitmap(){
        return new RequestBuilder(this,context);
    }

    @Override
    public RequestBuilder load(String string) {
        return asBitmap().load(string);
    }

    @Override
    public RequestBuilder load(File file) {
        return asBitmap().load(file);
    }
}
