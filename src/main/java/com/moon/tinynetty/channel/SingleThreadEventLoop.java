package com.moon.tinynetty.channel;

import com.moon.tinynetty.channel.nio.NioEventLoop;
import com.moon.tinynetty.util.concurrent.RejectedExecutionHandler;
import com.moon.tinynetty.util.concurrent.SingleThreadEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.Executor;

/**
 * @author Chanmoey
 * Create at 2024/2/7
 */
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {

    private static final Logger logger = LoggerFactory.getLogger(SingleThreadEventLoop.class);

    //任务队列的容量，默认是Integer的最大值
    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;

    protected SingleThreadEventLoop(EventLoopGroup parent, Executor executor,
                                    boolean addTaskWakesUp, Queue<Runnable> taskQueue, Queue<Runnable> tailTaskQueue,
                                    RejectedExecutionHandler rejectedExecutionHandler) {
        super(parent, executor, addTaskWakesUp, taskQueue, rejectedExecutionHandler);
    }


    @Override
    public EventLoopGroup parent() {
        return null;
    }

    @Override
    public EventLoop next() {
        return this;
    }

    public void register(ServerSocketChannel channel, NioEventLoop nioEventLoop) {
         // 如果执行该方法的线程就是执行器中的线程，直接执行方法即可
        if (inEventLoop(Thread.currentThread())) {
            register0(channel, nioEventLoop);
        } else {
            // 第一次向单线程执行器中提交任务，执行器开始执行
            nioEventLoop.execute(() -> {
                register(channel, nioEventLoop);
                logger.info("服务器的channel已注册到多路复用器上了！:{}",Thread.currentThread().getName());
            });
        }
    }

    public void registerRead(SocketChannel channel, NioEventLoop nioEventLoop) {
        if (nioEventLoop.inEventLoop(Thread.currentThread())) {
            register0(channel, nioEventLoop);
        } else {
            nioEventLoop.execute(() -> {
                register00(channel,nioEventLoop);
                logger.info("客户端的channel已注册到workgroup多路复用器上了！:{}",Thread.currentThread().getName());
            });
        }
    }

    public void register(SocketChannel channel,NioEventLoop nioEventLoop) {
        //如果执行该方法的线程就是执行器中的线程，直接执行方法即可
        if (nioEventLoop.inEventLoop(Thread.currentThread())) {
            register0(channel,nioEventLoop);
        }else {
            //在这里，第一次向单线程执行器中提交任务的时候，执行器终于开始执行了
            nioEventLoop.execute(new Runnable() {
                @Override
                public void run() {
                    register0(channel,nioEventLoop);
                    logger.info("客户端的channel已注册到workgroup多路复用器上了！:{}",Thread.currentThread().getName());
                }
            });
        }
    }

    private void register0(SocketChannel channel,NioEventLoop nioEventLoop) {
        try {
            channel.configureBlocking(false);
            channel.register(nioEventLoop.unwrappedSelector(), SelectionKey.OP_CONNECT);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void register00(SocketChannel channel,NioEventLoop nioEventLoop) {
        try {
            channel.configureBlocking(false);
            channel.register(nioEventLoop.unwrappedSelector(), SelectionKey.OP_READ);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void register0(ServerSocketChannel channel,NioEventLoop nioEventLoop) {
        try {
            channel.configureBlocking(false);
            channel.register(nioEventLoop.unwrappedSelector(), SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
