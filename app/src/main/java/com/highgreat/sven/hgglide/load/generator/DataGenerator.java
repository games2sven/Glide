package com.highgreat.sven.hgglide.load.generator;

import com.highgreat.sven.hgglide.cache.Key;


public interface DataGenerator {

    interface DataGeneratorCallback{

        enum DataSource{
            REMOTE,
            CACHE
        }

        void onDataReady(Key sourceKey, Object data, DataSource dataSource);

        void onDataFetcherFailed(Key sourceKey,Exception e);
    }

    boolean startNext();

    void cancel();

}
