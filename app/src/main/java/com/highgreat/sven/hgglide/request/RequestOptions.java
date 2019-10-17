package com.highgreat.sven.hgglide.request;

public class RequestOptions {

    private int placeholderId;
    private int errorId;
    private int overrideHeight = -1;
    private int overrideWidth = -1;

    public RequestOptions placeholder(int resourceId){
        this.placeholderId = resourceId;
        return this;
    }

    public RequestOptions error(int resourceId){
        this.errorId = resourceId;
        return this;
    }

    public final int getErrorId() {
        return errorId;
    }

    public final int getPlaceholderId() {
        return placeholderId;
    }

    public RequestOptions override(int width,int height){
        this.overrideHeight = height;
        this.overrideWidth = width;
        return this;
    }

    public final int getOverrideWidth(){
        return overrideWidth;
    }

    public final int getOverrideHeight(){
        return overrideHeight;
    }

}
