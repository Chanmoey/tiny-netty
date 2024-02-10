package com.moon.tinynetty.util.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @author Chanmoey
 * Create at 2024/2/11
 */
public class ThreadPerTaskExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPerTaskExecutor.class);

    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException(("threadFactory"));
        }
        this.threadFactory = threadFactory;
    }

    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
        logger.info("真正执行任务的线程被创建了！");
    }
}
