package io.echo.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IoEchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9001);
        System.out.println("echo server started: " + serverSocket);

        int i = 0;
        while (i++ < 3) {
            Socket socket = serverSocket.accept();
            System.out.println("incoming connection: " + socket);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            int read;
            byte[] buf = new byte[1024];
            while ((read = in.read(buf)) != -1) {
                System.out.println("read bytes: " + read);
                out.write(buf, 0, read);
            }

            socket.close();
            System.out.println("socket closed");
        }

        serverSocket.close();
        System.out.println("echo server finished");
    }
}

