package com.highgreat.sven.hgglide.load.model.data;

/**
 * 负责获取数据
 */
public interface DataFetcher<Data> {

    interface DataFetcherCallBack<Data>{
        //数据加载完成
        void onFetcherReady(Data data);

        //加载失败
        void onLoadFailed(Exception e);
    }

    void cancel();

    void loadData(DataFetcherCallBack<Data> callBack);

    Class<Data> getDataClass();

}
