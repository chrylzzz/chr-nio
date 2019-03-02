package com.lnsoft.bio.threadPool;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程的阻塞IO,但是使用线程池，支持多个客户端访问，但还是阻塞的
 * Created By Chr on 2019/3/1/0001.
 */
public class ServerThreadPool {
    public static void main(String args[]) {
        //用线程池和new Thread(task);的区别：线程太多，cpu剧增，直到服务端崩掉
        //线程池的优点：可以指定线程的数量，比如说60个，请求太多，会放入到线程等待队列，等待队列也满了，会拒接，但是线程不会一直增多而导致cpu崩掉

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        RequestHandler requestHandler = new RequestHandler();
        try (ServerSocket serverSocket = new ServerSocket(7777)) {
            System.out.println("NIOServer has started,listening on port : " + serverSocket.getLocalSocketAddress());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection from : " + clientSocket.getRemoteSocketAddress());
                executorService.submit(new ClientHandler(clientSocket, requestHandler));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
