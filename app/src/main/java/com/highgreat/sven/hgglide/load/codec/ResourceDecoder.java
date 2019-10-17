package com.highgreat.sven.hgglide.load.codec;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Bitmap解码器
 * @param <T>
 */
public interface ResourceDecoder<T> {

    boolean handles( T source) throws IOException;
    Bitmap decode(T source ,int width,int height) throws IOException;
}
