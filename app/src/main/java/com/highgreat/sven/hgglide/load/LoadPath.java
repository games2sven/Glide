package com.highgreat.sven.hgglide.load;

import android.graphics.Bitmap;

import com.highgreat.sven.hgglide.load.codec.ResourceDecoder;

import java.io.IOException;
import java.util.List;

public class LoadPath<Data> {

    private final Class<Data> dataClass;
    private final List<ResourceDecoder<Data>> decoders;

    public LoadPath(Class<Data> dataClass, List<ResourceDecoder<Data>> decoder){
        this.dataClass = dataClass;
        this.decoders = decoder;
    }

    public Bitmap runLoad(Data data, int width, int height){
        Bitmap result = null;
        for (ResourceDecoder<Data> decoder : decoders) {
            try {
                if(decoder.handles(data)){
                    result = decoder.decode(data,width,height);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(result != null){
                break;
            }
        }
        return result;
    }


}
