package com.moon.tinynetty.util.concurrent;

/**
 * @author Chanmoey
 * Create at 2024/2/11
 */
public interface RejectedExecutionHandler {

    void rejected(Runnable task, SingleThreadEventExecutor executor);
}
