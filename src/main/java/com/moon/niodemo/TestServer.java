package com.moon.niodemo;

import com.moon.tinynetty.channel.nio.NioEventLoop;
import com.moon.tinynetty.util.concurrent.SingleThreadEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author jifali
 * Create at 2024/2/7
 */
public class TestServer {

    private static final Logger logger = LoggerFactory.getLogger(TestServer.class);

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, serverSocketChannel);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(10086));
        // 创建读写事件执行器
        NioEventLoop singleThreadEventExecutor = new NioEventLoop();
        while (true) {
            logger.info("新一轮探测新连接");
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = channel.accept();
                    singleThreadEventExecutor.register(socketChannel, singleThreadEventExecutor);
                    logger.info("客户端在main函数中连接成功！");
                    socketChannel.write(ByteBuffer.wrap("我发送成功了".getBytes(StandardCharsets.UTF_8)));
                    logger.info("main函数向客户端发送数据成功！");
                }
            }
        }
    }
}
