package io.echo.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class IoEchoClient {

    public static void main(String[] args) throws IOException {
        System.out.println("echo client is starting");
        Socket socket = new Socket("localhost", 9001);
        System.out.println("echo client started: " + socket);

        String msg = "abcdefghijklmnopqrstuvwxyz";

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        byte[] bytes = msg.getBytes();
        out.write(bytes);

        int totalBytesReceived = 0;
        int bytesReceived;
        while (totalBytesReceived < bytes.length) {
            if ((bytesReceived = in.read(bytes, totalBytesReceived, bytes.length - totalBytesReceived)) == -1)
                throw new SocketException("Connection closed prematurely");
            totalBytesReceived += bytesReceived;
        }

        System.out.println("echo client received: " + new String(bytes));

        socket.close();

        System.out.println("echo client finished");
    }
}