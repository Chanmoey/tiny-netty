package com.moon.tinynetty.test;

import com.moon.tinynetty.bootstrap.Bootstrap;
import com.moon.tinynetty.channel.nio.NioEventLoopGroup;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @author Chanmoey
 * Create at 2024/2/11
 */
public class ClientTest {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).
                socketChannel(socketChannel);
        bootstrap.connect("127.0.0.1",8080);
    }

}
