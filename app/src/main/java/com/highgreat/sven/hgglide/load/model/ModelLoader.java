package com.highgreat.sven.hgglide.load.model;


import com.highgreat.sven.hgglide.cache.Key;
import com.highgreat.sven.hgglide.load.model.data.DataFetcher;

/**
 *
 * @param <Model> v表示的是数据的来源
 * @param <Data>  加载成功后的数据类型（inputstream，byte[]）
 */

public interface ModelLoader<Model,Data> {

    interface ModelLoaderFactory<Model ,Data>{
        ModelLoader<Model,Data> build(ModelLoaderRegistry registry);
    }

    class LoadData<Data>{
        //缓存的key
        public final Key key;

        public DataFetcher<Data> fetcher;

        public LoadData(Key key, DataFetcher<Data> fetcher) {
            this.key = key;
            this.fetcher = fetcher;
        }
    }

    /**
     * 判断处理对应model的数据
     */
    boolean handles(Model model);

    /**
     * 创建加载的数据方式
     */
     LoadData<Data> buildData(Model model);

}
