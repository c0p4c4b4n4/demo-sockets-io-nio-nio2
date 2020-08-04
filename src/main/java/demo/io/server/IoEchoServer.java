package demo.io.server;

import demo.common.Demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IoEchoServer extends Demo {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7000);
        System.out.println("echo server started: " + serverSocket);

        int i = 0;
        while (i++ < 3) {
            Socket socket = serverSocket.accept();
            System.out.println("incoming connection: " + socket);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            int read;
            byte[] bytes = new byte[4];
            while ((read = is.read(bytes)) != -1) {
                System.out.println("server read: " + read);
                System.out.println("server received: " + new String(bytes, 0, read, StandardCharsets.UTF_8));

                sleep(1);
                os.write(bytes, 0, read);
            }

            socket.close();
            System.out.println("socket closed");
        }

        serverSocket.close();
        System.out.println("echo server finished");
    }
}

