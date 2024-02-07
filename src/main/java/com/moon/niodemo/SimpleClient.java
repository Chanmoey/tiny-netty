package com.moon.niodemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Chenmoey
 * Create at
 */
public class SimpleClient {

    public static void main(String[] args) throws IOException {

        String serverHost = args[0];
        int serverPort = Integer.parseInt(args[1]);

        // 创建Channel
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        // 注册Selector并设置感兴趣的事件
        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_CONNECT);
        // 连接服务器
        channel.connect(new InetSocketAddress(serverHost, serverPort));
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()) {
                    if (channel.finishConnect()) {
                        channel.register(selector, SelectionKey.OP_READ);
                        // 发送消息
                        channel.write(ByteBuffer.wrap("Hello, World, I am client".getBytes(StandardCharsets.UTF_8)));
                    }
                }
                else if (key.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int read = channel.read(buffer);
                    byte[] bytes = new byte[read];
                    buffer.flip();
                    buffer.get(bytes);
                    System.out.println(new String(bytes));
                }
            }
        }
    }
}
