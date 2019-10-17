package com.highgreat.sven.hgglide.load;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.highgreat.sven.hgglide.Glide;
import com.highgreat.sven.hgglide.cache.DiskCache;
import com.highgreat.sven.hgglide.cache.Key;
import com.highgreat.sven.hgglide.cache.recycle.Resource;
import com.highgreat.sven.hgglide.load.generator.DataCacheGenerator;
import com.highgreat.sven.hgglide.load.generator.DataGenerator;
import com.highgreat.sven.hgglide.load.generator.SourceGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DecodeJob implements Runnable, DataGenerator.DataGeneratorCallback {

    private static final String TAG = "DecodeJob";
    private boolean isCancelled;
    private final Callback callback;
    private final int width;
    private final int height;
    private final Object model;
    private final Glide glide;

    private DataGenerator currentGenerator;
    private boolean isCallbackNotified;

    private final Key loadKey;
    private Key sourceKey;

    private Stage stage;

    /**
     * 加载器加载完成后回调（目前只有InputStream类型数据）
     * @param sourceKey
     * @param data
     * @param dataSource
     */
    @Override
    public void onDataReady(Key sourceKey, Object data, DataSource dataSource) {
        this.sourceKey = sourceKey;
        Log.e(TAG, "加载成功,开始解码数据");
        Log.i("Sven","加载成功");
        runLoadPath(data,dataSource);
    }

    private<Data> void runLoadPath(Data data, DataSource dataSource) {
        LoadPath<Data> loadPath = glide.getRegistry().getLoadPath((Class<Data>)data.getClass());
        Bitmap bitmap = loadPath.runLoad(data,width,height);
        if(bitmap != null){
            Log.e(TAG, "解码成功回调");
            notifyComplete(bitmap,dataSource);
        }else{
            Log.e(TAG, "解码失败，尝试使用下一个加载器");
            runGenerators();
        }
    }

    @Override
    public void onDataFetcherFailed(Key sourceKey, Exception e) {

    }

    interface Callback{
        void onResourceReady(Resource resource);

        void onLoadFailed(Throwable e);
    }

    DecodeJob(Context context, Object model, Key loadKey, int width, int height, Callback callback){
        glide = Glide.get(context);
        this.loadKey = loadKey;
        this.width = width;
        this.height = height;
        this.callback = callback;
        this.model = model;
    }


    public void cancel(){
        isCancelled = true;
        if(currentGenerator != null){
            currentGenerator.cancel();
        }
    }


    @Override
    public void run() {
        //由对应的fragment生命周期取消
        if(isCancelled){
            callback.onLoadFailed(new IOException("Canceled"));
            return;
        }
        stage = getNextStage(Stage.INITIALIZE);
        //下一个数据生成器
        currentGenerator = getNextGenerator();
        runGenerators();
    }

    private void runGenerators(){
        boolean isStarted = false;
        while(!isCancelled && currentGenerator != null && !isStarted){
            isStarted = currentGenerator.startNext();
            if(isStarted){
                break;
            }
            //执行下一个
            stage = getNextStage(stage);
            if (stage == Stage.FINISHED) {
               //状态结束,没有加载器能够加载对应数据
                break;
            }
            currentGenerator = getNextGenerator();
        }
        if((stage == Stage.FINISHED || isCancelled) && !isStarted){
            notifyFailed();
        }
    }

    private void notifyFailed() {
        //加载失败
        if (!isCallbackNotified) {
            isCallbackNotified = true;
            callback.onLoadFailed(new RuntimeException("Failed to load resource"));
        }
    }

    private void notifyComplete(final Bitmap bitmap,DataSource dataSource){
        if(dataSource == DataSource.REMOTE){
            glide.getDiskCache().put(sourceKey, new DiskCache.Writer() {
                @Override
                public boolean write(File file) {
                    FileOutputStream os = null;

                    try {
                        os = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,90,os);
                        return true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }finally {
                        if(null != os){
                            try {
                                os.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return false;
                }
            });
        }
        Resource resource = new Resource(bitmap);
        callback.onResourceReady(resource);
    }


    private DataGenerator getNextGenerator(){
        switch (stage){
            case DATA_CACHE:
                //使用磁盘加载器
                return new DataCacheGenerator(glide,model,this);
            case SOURCE:
                //使用源资源加载器
                return new SourceGenerator(glide,model,this);
            case FINISHED:
                return null;
                default:
                    throw new IllegalStateException("Unrecognized stage:"+stage);
        }
    }


    /**
     * 下一个状态
     */
    private Stage getNextStage(Stage current){
        switch (current){
            case INITIALIZE:
                return Stage.DATA_CACHE;
            case DATA_CACHE:
                return Stage.SOURCE;
            case SOURCE:
            case FINISHED:
                return Stage.FINISHED;
                default:
                    throw new IllegalArgumentException("Unrecognized stage: " + current);
        }
    }


    /**
     * 当前状态
     */
     private enum Stage{
        INITIALIZE,
        DATA_CACHE,
        SOURCE,
        FINISHED,
    }

}
