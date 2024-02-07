package com.moon.tinynetty.channel.nio;

import com.moon.tinynetty.util.concurrent.SingleThreadEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author jifali
 * Create at 2024/2/7
 */
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SingleThreadEventLoop.class);

    public void register(SocketChannel socketChannel, NioEventLoop nioEventLoop) {
        // 如果调用线程为执行器线程，则直接执行
        if (inEventLoop(Thread.currentThread())) {
            register0(socketChannel, nioEventLoop);
        } else {
            // 提交一个注册Select的任务
            this.execute(() -> {
                register0(socketChannel, nioEventLoop);
            });
        }
    }

    private void register0(SocketChannel channel, NioEventLoop nioEventLoop) {
        try {
            channel.configureBlocking(false);
            channel.register(nioEventLoop.selector(), SelectionKey.OP_READ);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
