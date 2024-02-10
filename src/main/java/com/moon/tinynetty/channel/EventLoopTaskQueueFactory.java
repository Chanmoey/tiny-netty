package com.moon.tinynetty.channel;

import java.util.Queue;

/**
 * @author Chanmoey
 * @date @date 2024/2/11
 */
public interface EventLoopTaskQueueFactory {


    Queue<Runnable> newTaskQueue(int maxCapacity);
}
