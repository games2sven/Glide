package com.highgreat.sven.hgglide.load.generator;

import android.util.Log;

import com.highgreat.sven.hgglide.Glide;
import com.highgreat.sven.hgglide.cache.Key;
import com.highgreat.sven.hgglide.load.model.ModelLoader;
import com.highgreat.sven.hgglide.load.model.data.DataFetcher;

import java.io.File;
import java.util.List;

public class DataCacheGenerator implements DataGenerator, DataFetcher.DataFetcherCallBack {

    private static final String TAG = "DataCacheGenerator";

    private List<ModelLoader<File,?>> modelLoaderList;
    private int sourceIdIndex = -1;
    private final Glide glide;
    private List<Key> keys;
    private File chcheFile;
    private Key sourceKey;
    private int modelLoaderIndex;
    private ModelLoader.LoadData<?> loadData;
    private final DataGeneratorCallback cb;

    public DataCacheGenerator(Glide glide,Object model,DataGeneratorCallback cb){
        this.glide = glide;
        this.cb = cb;
        //获得对应类型的所有key (当前只有ObjectKey用于磁盘缓存，磁盘缓存不需要宽、高等)
        //即如果文件缓存 获得缓存的key
        keys = glide.getRegistry().getKeys(model);
    }


    @Override
    public boolean startNext() {
        Log.e(TAG, "磁盘加载器开始加载");
        while(modelLoaderList == null){
            sourceIdIndex++;
            if(sourceIdIndex >= keys.size()){
                return false;
            }
            Key sourceId = keys.get(sourceIdIndex);
            //获得磁盘缓存的文件
            chcheFile = glide.getDiskCache().get(sourceId);
            if(chcheFile != null){
                sourceKey = sourceId;
                Log.e(TAG, "获得所有文件加载器");
                //获得所有的文件加载器
                modelLoaderList = glide.getRegistry().getModelLoaders(chcheFile);
                modelLoaderIndex = 0;
            }
        }

        boolean started = false;
        //找出好几个File为Model的 Loader 直到确定一个完全满足
        // 即 能够由此Loader解析完成
        while(!started && hasNextModelLoader()){
            ModelLoader<File ,?> modelLoader = modelLoaderList.get(modelLoaderIndex++);
            loadData = modelLoader.buildData(chcheFile);
            Log.e(TAG, "获得加载设置数据");
            //是否可以把此loader加载的Data 解码出Bitmap
            if (loadData != null && glide.getRegistry().hasLoadPath(loadData.fetcher.getDataClass
                    ())) {
                Log.e(TAG, "加载设置数据输出数据对应能够查找有效的解码器路径,开始加载数据");
                started = true;
                loadData.fetcher.loadData(this);
            }
        }
        return started;
    }

    private boolean hasNextModelLoader(){
        return modelLoaderIndex < modelLoaderList.size();
    }

    @Override
    public void cancel() {
        if(loadData != null){
            loadData.fetcher.cancel();
        }
    }

    @Override
    public void onFetcherReady(Object data) {
        Log.e(TAG, "加载器加载数据成功回调");
        cb.onDataReady(sourceKey,data,DataGeneratorCallback.DataSource.CACHE);
    }

    @Override
    public void onLoadFailed(Exception e) {
        Log.e(TAG, "加载器加载数据失败回调");
        cb.onDataFetcherFailed(sourceKey,e);
    }
}
