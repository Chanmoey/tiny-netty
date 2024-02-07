package com.moon.niodemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author jifali
 * Create at 2024/2/7
 */
public class SimpleServer {

    private static final List<SocketChannel> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(10086));
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel client = serverSocketChannel.accept();
                    System.out.println("新连接" + client.getRemoteAddress());
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    ByteBuffer wrap = ByteBuffer.wrap("Hello, Client, I am server".getBytes(StandardCharsets.UTF_8));
                    client.write(wrap);
                } else if (key.isReadable()) {
                    //同样有两种方式得到客户端的channel，这里只列出一种
                    SocketChannel channel = (SocketChannel) key.channel();
                    //分配字节缓冲区来接受客户端传过来的数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    //向buffer写入客户端传来的数据
                    int len = channel.read(buffer);
                    if (len == -1) {
                        channel.close();
                        break;
                    } else {
                        //切换buffer的读模式
                        buffer.flip();
                        System.out.println(Charset.defaultCharset().decode(buffer).toString());
                    }
                }
            }
        }
    }
}
