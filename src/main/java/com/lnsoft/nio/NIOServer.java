package com.lnsoft.nio;

import com.lnsoft.bio.threadPool.RequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created By Chr on 2019/3/2/0002.
 */
public class NIOServer {
    public static void main(String args[]) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9999));
        System.out.println("NIOServer has started,listening on port : " + serverSocketChannel.getLocalAddress());
        Selector selector = Selector.open();
        //注册
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        RequestHandler requestHandler = new RequestHandler();
        //不断循环监听channel的状态改变 accept read write
        while (true) {
            //轮询连接，会阻塞，和serverSocket.accept();异曲同工
            int select = selector.select();
            if (select == 0) {
                continue;
            }
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();
            while (iterator.hasNext()) {
                //
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = channel.accept();
                    System.out.println("Connection from : " + clientChannel.getLocalAddress());
                    clientChannel.configureBlocking(false);
                    //通过register改变channel要进行的操作
                    clientChannel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    channel.read(buffer);
                    String request = new String(buffer.array()).trim();
                    buffer.clear();
                    System.out.println(String.format("From %s %s", channel.getRemoteAddress(), request));
                    String response = requestHandler.handle(request);
                    channel.write(ByteBuffer.wrap(response.getBytes()));
                }
                iterator.remove();
            }
        }
    }
}
