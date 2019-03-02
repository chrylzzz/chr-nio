package com.lnsoft.bio.threadPool;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * Created By Chr on 2019/3/1/0001.
 */
public class ClientHandler implements Callable {

    private final Socket clientSocket;
    private final RequestHandler requestHandler;

    public ClientHandler(Socket clientSocket,RequestHandler requestHandler) {
        this.clientSocket = clientSocket;
        this.requestHandler = requestHandler;
    }

    @Override
    public Object call() throws Exception {
        try (Scanner input = new Scanner(clientSocket.getInputStream())) {
            while (true) {
                String request = input.nextLine();  //阻塞的 客户端如果没有数据发送过来，这个
                if ("quit".equals(request)) {
                    break;
                }
                //2，Threads 处理
                System.out.println(String.format("From %s : %s", clientSocket.getRemoteSocketAddress(), request));
                //使用handle处理业务
                String response = requestHandler.handle(request);
                clientSocket.getOutputStream().write(response.getBytes());
            }

        } catch (IOException e) {
            System.out.println("Caught exception : " + e);
            e.printStackTrace();
        }
        return null;
    }
}
