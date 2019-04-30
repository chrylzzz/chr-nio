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
        //打开serverSocketChannel,监听客户端的连接,所有客户端的父管道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置连接为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //socketAddress
        serverSocketChannel.bind(new InetSocketAddress(9999));
        System.out.println("NIOServer has started,listening on port : " + serverSocketChannel.getLocalAddress());
        //打开selector多路复用
        Selector selector = Selector.open();
        //创建reactor线程并启动
//        new Thread(new ReactorTask()).start();
        //serverSocketChannel注册到reactor线程的selector多路复用,监听accept事件,启动selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//接受
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        RequestHandler requestHandler = new RequestHandler();
        //不断循环监听channel的状态改变 accept read write
        while (true) {
            int select = selector.select();//轮训就绪的key(Channel)
            if (select == 0) {
                continue;
            }
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();
            while (iterator.hasNext()) {
                //
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {//轮训,如果有客户端请求接入
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = channel.accept();//处理请求,完成三次握手建立物理链路
                    System.out.println("Connection from : " + clientChannel.getLocalAddress());
                    //客户端链路设置为非阻塞
                    clientChannel.configureBlocking(false);
//                    clientChannel.socket().setReuseAddress(true);
                    //通过register改变channel要进行的操作,向selector注册监听读操作
                    //讲新接入的客户端注册到reactor线程的selector多路复用器上,监听读操作,监听客户端发送的网络消息
                    clientChannel.register(selector, SelectionKey.OP_READ);//如果是read则有就绪的数据包需要读取
                }
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    channel.read(buffer);//异步读取客户端的消息到buffer
                    //#######################

                    //#######################
                    String request = new String(buffer.array()).trim();
                    buffer.clear();
                    System.out.println(String.format("From %s %s", channel.getRemoteAddress(), request));
                    String response = requestHandler.handle(request);
                    channel.write(ByteBuffer.wrap(response.getBytes()));//异步写消息到socketChannel,发送给客户端
                }
                iterator.remove();
            }
        }
    }
}
