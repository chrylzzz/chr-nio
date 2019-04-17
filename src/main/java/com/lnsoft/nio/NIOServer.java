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
 * NIO-Socket
 * <p>
 * Created By Chr on 2019/3/2/0002.
 */
public class NIOServer {
    public static void main(String args[]) throws IOException {
        //初始化SocketChannel，
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置非阻塞
        serverSocketChannel.configureBlocking(false);//为什么要可以设置非阻塞，兼容BIO阻塞方式
        //监听端口
        serverSocketChannel.bind(new InetSocketAddress(9999));
        System.out.println("NIOServer has started,listening on port : " + serverSocketChannel.getLocalAddress());

        //初始化selector
        Selector selector = Selector.open();
        //注册：channel注册在selector上，但是不分配线程；第二个参数：如果有客户端连接，selector就设置为accept
        //为什么注册serverSocketChannel ？
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //缓冲区，为什么是1024？    1024作为读写大小比较合适
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        RequestHandler requestHandler = new RequestHandler();
        //不断循环监听channel的状态改变 accept read write
        while (true) {
            //轮询连接，会阻塞，和serverSocket.accept();异曲同工
            int select = selector.select();
            if (select == 0) {
                continue;
            }
            //SelectionKey代表服务端的channel，可以通过这个 也可以拿到客户端的channel（类似于服务端和客户端的channel）
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();
            while (iterator.hasNext()) {
                //
                SelectionKey key = iterator.next();
                //查看SelectionKey的状态，是否已经连接上来，之前设置过SelectionKey.OP_ACCEPT
                if (key.isAcceptable()) {
                    //根据SelectionKey拿到服务端的channel
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    //根据服务端的channel可以拿到客户端的channel
                    SocketChannel clientChannel = channel.accept();

                    System.out.println("Connection from : " + clientChannel.getLocalAddress());
                    clientChannel.configureBlocking(false);
                    //通过register改变channel要进行的操作，readable/writable
                    clientChannel.register(selector, SelectionKey.OP_READ);
                }
                //查看SelectionKey的状态
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    //数据读到缓冲区，buffer相当于读写之间的桥梁，buffer就像一个数组
                    channel.read(buffer);
                    String request = new String(buffer.array()).trim();
                    buffer.clear();
                    System.out.println(String.format("From %s %s", channel.getRemoteAddress(), request));
                    //回写数据
                    String response = requestHandler.handle(request);
                    channel.write(ByteBuffer.wrap(response.getBytes()));
                }
                iterator.remove();
            }
        }
    }
}
