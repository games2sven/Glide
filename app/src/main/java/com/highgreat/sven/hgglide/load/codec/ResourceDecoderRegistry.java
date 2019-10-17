package com.highgreat.sven.hgglide.load.codec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResourceDecoderRegistry {

    private final List<Entry<?>> entries = new ArrayList<>();


    private static class Entry<T>{

        private final Class<T> dataClass;
        final ResourceDecoder<T> decoder;


        public Entry(Class<T> dataClass,ResourceDecoder<T> decoder){
            this.dataClass = dataClass;
            this.decoder = decoder;
        }

        public boolean handles(Class<?> dataClass){
            return this.dataClass.isAssignableFrom(dataClass);
        }

    }

    public <Data> List<ResourceDecoder<Data>> getDecoders(Class<Data> dataClass){
        List<ResourceDecoder<Data>> decoders = new ArrayList<>();
        for (Entry<?> entry : entries) {
            if(entry.handles(dataClass)){
                decoders.add((ResourceDecoder<Data>) entry.decoder);
            }
        }
        return decoders;
    }


    public <T> void add(Class<T> dataClass, ResourceDecoder<T> decoder) {
        entries.add(new Entry<>(dataClass,decoder));
    }
}
