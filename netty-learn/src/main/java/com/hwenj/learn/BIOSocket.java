package com.hwenj.learn;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author hwenj
 * @since 2022/9/18
 */
public class BIOSocket {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9854);
        Executor executor = Executors.newCachedThreadPool();
        System.out.println("服务器启动了");
        while (true) {
            System.out.println("等待链接——");
            Socket accept = serverSocket.accept();
            executor.execute(() -> {
                try {
                    System.out.println("链接到一个客户端");
                    handle(accept);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        accept.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("链接关闭");
                    }
                }
            });
        }

    }

    public static void handle(Socket socket) throws IOException {
        byte[] bytes = new byte[1024];
        // socket获取输入流
        InputStream inputStream = socket.getInputStream();
        while (true) {
            int read = inputStream.read(bytes);
            if (read != -1) {
                System.out.println(new String(bytes, 0, read));
            } else {
                break;
            }
        }


    }
}
