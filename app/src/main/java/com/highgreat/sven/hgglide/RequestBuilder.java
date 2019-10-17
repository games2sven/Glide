package com.highgreat.sven.hgglide;

import android.content.Context;
import android.widget.ImageView;

import com.highgreat.sven.hgglide.request.Request;
import com.highgreat.sven.hgglide.request.RequestOptions;

import java.io.File;

public class RequestBuilder implements ModelTypes<RequestBuilder> {

    private final RequestManager requestManager;
    private final RequestOptions defaultRequestOptions;
    private final Context context;
    protected  RequestOptions requestOptions;

    private Object model;

    protected RequestBuilder(RequestManager requestManager, Context context){
        this.requestManager = requestManager;
        this.defaultRequestOptions = Glide.get(context).getDefaultRequestOptions();
        this.context = context;
        //自己的options默认的不能修改
        this.requestOptions = defaultRequestOptions;
    }

    public RequestBuilder apply(RequestOptions requestOptions){
        this.requestOptions = requestOptions;
        return this;
    }

    private RequestBuilder loadGeneric(Object model){
        this.model = model;
        return this;
    }

    @Override
    public RequestBuilder load(String string) {
        return loadGeneric(string);
    }

    @Override
    public RequestBuilder load(File file) {
        return loadGeneric(file);
    }

    public Target into(ImageView view){
        Target target = new Target(view);
        Request request = new Request(context,model,requestOptions,target);
        target.setRequest(request);
        //追踪并开始执行请求
        requestManager.track(request);
        return target;
    }

}
