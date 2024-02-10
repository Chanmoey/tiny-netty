package com.moon.tinynetty.util.concurrent;

/**
 * @author Chanmoey
 * @date @date 2024/2/10
 */
public interface EventExecutor extends EventExecutorGroup{

    EventExecutorGroup parent();

    boolean inEventLoop(Thread thread);
}
