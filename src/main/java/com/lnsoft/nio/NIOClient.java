package com.lnsoft.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created By Chr on 2019/4/29.
 */
public class NIOClient {
    private static final int BUFFER_SIZE = 1024;

    public static void main(String args[]) throws IOException {
        //打开SocketChannel,绑定客户端地址,不绑定的话默认分配可用的
        SocketChannel clientChannel = SocketChannel.open();

        //设置socketChannel为非阻塞模式,并且设置为tcp连接的参数
        clientChannel.configureBlocking(false);
//        clientChannel.bind(new InetSocketAddress(xxx));

        Socket socket = new Socket();
        socket.setReuseAddress(true);
        socket.setReceiveBufferSize(BUFFER_SIZE);
        socket.setSendBufferSize(BUFFER_SIZE);
        //异步连接服务端
        boolean connected = clientChannel.connect(new InetSocketAddress(999));
        Selector selector = Selector.open();
        //判断是否连接成功,成功,直接注册度状态到多路复用器,
        //未成功:异步连接,返回false,说明哭嘟嘟已发送sync包,服务端未返回ack包,物理链路没有连接
        if (connected) {
//            clientChannel.register(selector, SelectionKey.OP_READ,iohandler);
        } else {
//            clientChannel.register(selector, SelectionKey.OP_CONNECT,iohandler);
        }

//        clientChannel.register(selector, SelectionKey.OP_CONNECT,iohandler);


        //...
    }
}
