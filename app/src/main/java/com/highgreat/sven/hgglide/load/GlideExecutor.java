package com.highgreat.sven.hgglide.load;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//线程池执行器
public class GlideExecutor {

    private static int bestThreadCount;
    public static int calculateBestThreadCount(){
        if(bestThreadCount == 0){
            //获取可用处理器个数
            bestThreadCount = Math.min(4,Runtime.getRuntime().availableProcessors());
        }
        return bestThreadCount;
    }

    private static final class DefaultThreadFactory implements ThreadFactory{

        private int threadNum;

        @Override
        public Thread newThread(Runnable r) {
            final Thread result = new Thread(r,"glide-thread-"+threadNum);
            threadNum++;
            return result;
        }
    }


    public static ThreadPoolExecutor newExecutor(){
        int threadCount = calculateBestThreadCount();

        return new ThreadPoolExecutor(
                threadCount/* corePoolSize */,
                threadCount/* maximumPoolSize*/,
                0/*keepAliveTime*/,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>(),
                new DefaultThreadFactory()
        );

    }

}
