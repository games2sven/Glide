package com.highgreat.sven.hgglide.load.model.data;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//本地文件加载数据  file://
public class FileUriFetcher implements DataFetcher<InputStream> {

    private final Uri uri;
    private final ContentResolver cr;

    public FileUriFetcher(Uri uri, ContentResolver cr) {
        this.uri = uri;
        this.cr = cr;
    }


    @Override
    public void cancel() {

    }

    @Override
    public void loadData(DataFetcherCallBack<InputStream> callBack) {
        InputStream is = null;
        try {
            is = cr.openInputStream(uri);
            callBack.onFetcherReady(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callBack.onLoadFailed(e);
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
}
