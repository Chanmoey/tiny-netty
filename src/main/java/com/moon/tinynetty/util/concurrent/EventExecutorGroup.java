package com.moon.tinynetty.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author Chanmoey
 * @date @date 2024/2/10
 */
public interface EventExecutorGroup extends Executor {

    EventExecutor next();

    /**
     * 优雅关闭线程池
     */
    void shutdownGracefully();

    boolean isTerminated();

    void awaitTermination(Integer integer, TimeUnit timeUnit) throws InterruptedException;
}
