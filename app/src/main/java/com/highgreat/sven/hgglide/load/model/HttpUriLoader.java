package com.highgreat.sven.hgglide.load.model;

import android.net.Uri;

import com.highgreat.sven.hgglide.load.Objectkey;
import com.highgreat.sven.hgglide.load.model.data.HttpUriFetcher;

import java.io.InputStream;

public class HttpUriLoader implements ModelLoader<Uri, InputStream>{

    @Override
    public boolean handles(Uri uri) {
        String scheme = uri.getScheme();
        return scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https");
    }

    @Override
    public LoadData<InputStream> buildData(Uri uri) {
        return new LoadData<InputStream>(new Objectkey(uri),new HttpUriFetcher(uri));
    }

    public static class Factory implements ModelLoader.ModelLoaderFactory<Uri,InputStream>{

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry registry) {
            return new HttpUriLoader();
        }
    }

}
