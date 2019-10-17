package com.highgreat.sven.hgglide.load.model;

import java.util.ArrayList;
import java.util.List;

//模型加载登记
public class ModelLoaderRegistry {

    private List<Entry<?,?>> entries = new ArrayList<>();

    public synchronized<Model,Data> void add(Class<Model> modelClass,
                                             Class<Data> dataClass,ModelLoader.ModelLoaderFactory<Model,Data> factory){
        entries.add(new Entry<>(modelClass,dataClass,factory));
    }

    /**
     * 获取对应model与data类型的modelloader
     * @param <Model>
     * @param <Data>
     * @return
     */
    public <Model,Data> ModelLoader<Model,Data> build(Class<Model> modelClass,Class<Data> dataClass){
        List<ModelLoader<Model,Data>> loaders = new ArrayList<>();
        for (Entry<?, ?> entry : entries) {
            //是我们需要的Model与Data类型的Loader
            if(entry.handles(modelClass,dataClass)){
                loaders.add((ModelLoader<Model, Data>) entry.factory.build(this));
            }
        }

        //找到多个匹配的loader
        //比如 Environment.getExternalStorageDirectory()+"/main.jpg" 和"https://ss1.bdstatic" +".com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2669567003," +"3609261574&fm=27&gp=0.jpg22222222asads"
        if(loaders.size() >1){
           return  new MultiModelLoader<>(loaders);
        }else{
            return loaders.get(0);
        }
    }

    private static class Entry<Model,Data>{
        Class<Model> modelClass;
        Class<Data> dataClass;
        ModelLoader.ModelLoaderFactory<Model,Data> factory;

        public Entry(Class<Model> modelClass, Class<Data> dataClass, ModelLoader.ModelLoaderFactory<Model, Data> factory) {
            this.modelClass = modelClass;
            this.dataClass = dataClass;
            this.factory = factory;
        }

        boolean handles(Class<?> modelClass,Class<?> dataClass){
            //A.isAssignableFrom(B) B和A是同一个类型 或者B是A的子类
            return this.modelClass.isAssignableFrom(modelClass) &&
                    this.dataClass.isAssignableFrom(dataClass);
        }

        public boolean handles(Class<?> modelClass){
            return this.modelClass.isAssignableFrom(modelClass);
        }


    }

    /**
     * 获取符合model类型的loader集合
     * @param modelClass
     * @param <Model>
     * @return
     */
    public <Model> List<ModelLoader<Model,?>> getModelLoaders(Class<Model> modelClass){
        List<ModelLoader<Model,?>> modelLoaders = new ArrayList<>();
        for (Entry<?, ?> entry : entries) {
            //model符合的加入集合
            if(entry.handles(modelClass)){
                modelLoaders.add((ModelLoader<Model, ?>) entry.factory.build(this));
            }
        }
        return modelLoaders;
    }



}
