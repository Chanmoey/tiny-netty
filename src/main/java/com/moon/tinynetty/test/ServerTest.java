package com.moon.tinynetty.test;

import com.moon.tinynetty.bootstrap.ServerBootstrap;
import com.moon.tinynetty.channel.nio.NioEventLoopGroup;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

/**
 * @author Chanmoey
 * Create at 2024/2/11
 */
public class ServerTest {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);
        serverBootstrap.group(bossGroup,workerGroup).
                serverSocketChannel(serverSocketChannel);
        serverBootstrap.bind("127.0.0.1",8080);
    }
}
