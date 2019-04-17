package com.lnsoft.bio;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * BIO-Socket
 * <p>
 * 单线程阻塞IO，只能单线程等待请求
 * Created By Chr on 2019/3/1/0001.
 */
public class BIOServer {
    public static void main(String args[]) {

        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("BIOServer has started,listening on port : " + serverSocket.getLocalSocketAddress());
            while (true) {
                //1，等待客户端连接，会阻塞
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection from : " + clientSocket.getRemoteSocketAddress());
                try (Scanner input = new Scanner(clientSocket.getInputStream())) {
                    while (true) {
                        String request = input.nextLine();
                        if ("quit".equals(request)) {
                            break;
                        }
                        //2，Threads 处理
                        System.out.println(String.format("From %s : %s", clientSocket.getRemoteSocketAddress(), request));
                        //处理业务
                        String response = "From BIOServer Hello : " + request + ".\n";
                        clientSocket.getOutputStream().write(response.getBytes());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
