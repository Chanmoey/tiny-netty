package com.moon.tinynetty.channel;

import com.moon.tinynetty.util.NettyRuntime;
import com.moon.tinynetty.util.concurrent.DefaultThreadFactory;
import com.moon.tinynetty.util.concurrent.EventExecutor;
import com.moon.tinynetty.util.concurrent.EventExecutorChooserFactory;
import com.moon.tinynetty.util.concurrent.MultithreadEventExecutorGroup;
import com.moon.tinynetty.util.internal.SystemPropertyUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @author Chanmoey
 * Create at 2024/2/11
 */
public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup {

    private static final int DEFAULT_EVENT_LOOP_THREADS;

    //如果用户没有设定线程数量，则线程数默认使用这里的cpu核数乘2
    static {
        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));

    }

    //通常情况下，这个构造器会从子类被调用
    protected MultithreadEventLoopGroup(int nThreads, Executor executor, Object... args) {
        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, args);
    }

    protected MultithreadEventLoopGroup(int nThreads, ThreadFactory threadFactory, Object... args) {
        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, threadFactory, args);
    }

    protected MultithreadEventLoopGroup(int nThreads, Executor executor, EventExecutorChooserFactory chooserFactory,
                                        Object... args) {
        super(nThreads == 0 ? DEFAULT_EVENT_LOOP_THREADS : nThreads, executor, chooserFactory, args);
    }

    @Override
    protected ThreadFactory newDefaultThreadFactory() {
        return new DefaultThreadFactory(getClass(), Thread.MAX_PRIORITY);
    }

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }

    @Override
    protected abstract EventExecutor newChild(Executor executor, Object... args) throws Exception;
}
