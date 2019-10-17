package com.highgreat.sven.hgglide.load.model.data;

import android.net.Uri;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//http请求加载数据
public class HttpUriFetcher implements DataFetcher<InputStream>{

    private final Uri uri;
    //如果请求被取消
    private boolean isCanceled;

    public HttpUriFetcher(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void cancel() {
        isCanceled = true;
    }

    @Override
    public void loadData(DataFetcherCallBack<InputStream> callBack) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            URL url = new URL(uri.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            is = connection.getInputStream();
            int responseCode = connection.getResponseCode();
            if(isCanceled){
                return;
            }
            if(responseCode == HttpURLConnection.HTTP_OK){
                Log.i("Sven","http 加载成功");
                callBack.onFetcherReady(is);
            }else{
                callBack.onLoadFailed(new RuntimeException(connection.getResponseMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(null != connection){
                connection.disconnect();
            }
        }
    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
}
