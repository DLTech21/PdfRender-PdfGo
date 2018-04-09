package com.dl.pdfgo.render;


import com.dl.pdfgo.task.PriorityBlockingQueue;
import com.dl.pdfgo.task.XExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by donal on 2018/3/15.
 */

public class PdfRenderThreadPool {
    private static final int MAX_POOL_SIZE = 5;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit UNIT = TimeUnit.HOURS;
    private int corePoolSize = 2;
    private XExecutor executor;

    public XExecutor getExecutor() {
        if (executor == null) {
            synchronized (PdfRenderThreadPool.class) {
                if (executor == null) {
                    executor = new XExecutor(corePoolSize, MAX_POOL_SIZE, KEEP_ALIVE_TIME, UNIT,
                            new PriorityBlockingQueue<Runnable>(),
                            Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy());
                }
            }
        }
        return executor;
    }

    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize <= 0) corePoolSize = 1;
        if (corePoolSize > MAX_POOL_SIZE) corePoolSize = MAX_POOL_SIZE;
        this.corePoolSize = corePoolSize;
    }

    public void execute(Runnable runnable) {
        if (runnable != null) {
            getExecutor().execute(runnable);
        }
    }

    public void remove(Runnable runnable) {
        if (runnable != null) {
            getExecutor().remove(runnable);
        }
    }
}
