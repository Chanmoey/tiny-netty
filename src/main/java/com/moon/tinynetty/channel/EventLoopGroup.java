package com.moon.tinynetty.channel;

import com.moon.tinynetty.util.concurrent.EventExecutorGroup;

/**
 * @author Chanmoey
 * @date @date 2024/2/10
 */
public interface EventLoopGroup extends EventExecutorGroup {

    EventLoop next();
}
