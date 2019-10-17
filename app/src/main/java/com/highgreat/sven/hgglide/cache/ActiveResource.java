package com.highgreat.sven.hgglide.cache;

import com.highgreat.sven.hgglide.cache.recycle.Resource;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 正在使用中的图片资源
 */
public class ActiveResource {
    private Map<Key, ResourceWeakReference> weakReferenceMap = new HashMap<>();
    private final Resource.ResourceListener resourceListener;

    private ReferenceQueue<Resource> queue;
    private Thread cleanReferenceQueueTherad;
    private boolean isShutdown;

    public ActiveResource(Resource.ResourceListener resourceListener){
        this.resourceListener = resourceListener;
    }

    /**
     * 加入活动缓存
     */
    public void activete(Key key, Resource resource){
        resource.setResourceListener(key,resourceListener);
        weakReferenceMap.put(key,new ResourceWeakReference(key,resource,getReferenceQueue()));
    }

    /**
     * 移除活动缓存
     */
     public Resource deactivete(Key key){
         ResourceWeakReference reference = weakReferenceMap.remove(key);
         if(reference != null){
             return reference.get();
         }
         return null;
     }

    //弱引用
    static final class ResourceWeakReference extends WeakReference<Resource> {
        final Key key;

        public ResourceWeakReference(Key key,Resource referent, ReferenceQueue<? super Resource> q) {
            super(referent, q);
            this.key = key;
        }
    }

    private ReferenceQueue<? super Resource> getReferenceQueue(){
        if(null == queue){
            queue = new ReferenceQueue<>();
            cleanReferenceQueueTherad = new Thread() {
                @Override
                public void run() {
                    while(!isShutdown){
                        try {
                            ResourceWeakReference ref = (ResourceWeakReference)queue.remove();
                            weakReferenceMap.remove(ref.key);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            cleanReferenceQueueTherad.start();
        }
        return queue;
    }

    public Resource get(Key key){
        ResourceWeakReference resourceWeakReference = weakReferenceMap.get(key);
        if(resourceWeakReference != null){
            return resourceWeakReference.get();
        }
        return null;
    }


}
