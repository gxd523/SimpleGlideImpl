package com.impl.glide.cache;

import com.impl.glide.key.Key;
import com.impl.glide.recycle.Resource;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by guoxiaodong on 2020/3/1 15:43
 */
public class ActiveResource {
    private Map<Key, ResourceWeakReference> resourceMap = new HashMap<>();
    private ReferenceQueue<Resource> referenceQueue;
    private Thread cleanReferenceQueueThread;
    private Resource.ResourceListener resourceListener;
    private boolean isShutdown;

    public ActiveResource(Resource.ResourceListener resourceListener) {
        this.resourceListener = resourceListener;
    }

    /**
     * 加入活动缓存
     */
    public void active(Key key, Resource resource) {
        resource.setResourceListener(key, resourceListener);
        resourceMap.put(key, new ResourceWeakReference(key, resource, getReferenceQueue()));
    }

    /**
     * 从活动缓存中移除
     */
    public Resource deactive(Key key) {
        ResourceWeakReference resource = resourceMap.remove(key);
        if (resource != null) {
            return resource.get();
        }
        return null;
    }

    public Resource get(Key key) {
        ResourceWeakReference resource = resourceMap.get(key);
        if (resource != null) {
            return resource.get();
        }
        return null;
    }

    void shutdown() {
        isShutdown = true;
        if (cleanReferenceQueueThread != null) {
            cleanReferenceQueueThread.interrupt();// 强制关闭线程
            try {
                cleanReferenceQueueThread.join(TimeUnit.SECONDS.toMillis(5));
                if (cleanReferenceQueueThread.isAlive()) {
                    throw new RuntimeException("Failed join in time");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ReferenceQueue<? super Resource> getReferenceQueue() {
        if (referenceQueue == null) {
            referenceQueue = new ReferenceQueue<>();
            cleanReferenceQueueThread = new Thread() {
                @Override
                public void run() {
                    while (!isShutdown) {
                        try {// 被回收掉的引用
                            ResourceWeakReference referenceQueue = (ResourceWeakReference) ActiveResource.this.referenceQueue.remove();
                            resourceMap.remove(referenceQueue.key);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            cleanReferenceQueueThread.start();
        }
        return referenceQueue;
    }

    private static final class ResourceWeakReference extends WeakReference<Resource> {
        private final Key key;

        public ResourceWeakReference(Key key, Resource referent, ReferenceQueue<? super Resource> q) {
            super(referent, q);
            this.key = key;
        }
    }
}
