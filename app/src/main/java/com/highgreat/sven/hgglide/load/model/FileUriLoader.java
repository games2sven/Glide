package com.highgreat.sven.hgglide.load.model;

import android.content.ContentResolver;
import android.net.Uri;

import com.highgreat.sven.hgglide.load.Objectkey;
import com.highgreat.sven.hgglide.load.model.data.FileUriFetcher;

import java.io.InputStream;

public class FileUriLoader implements ModelLoader<Uri, InputStream> {

    private final ContentResolver contentResolver;

    public FileUriLoader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public boolean handles(Uri uri) {
        return contentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme());
    }

    @Override
    public LoadData<InputStream> buildData(Uri uri) {
        return new LoadData<>(new Objectkey(uri),new FileUriFetcher(uri,contentResolver));
    }

    public static class Factory implements ModelLoader.ModelLoaderFactory<Uri,InputStream>{

        private final ContentResolver contentResolver;

        public Factory(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry registry) {
            return new FileUriLoader(contentResolver);
        }
    }


}
