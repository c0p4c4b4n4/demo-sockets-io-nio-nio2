package io.echo.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IoEchoBufferedReaderServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9001);
        System.out.println("echo server started: " + serverSocket);

        int i = 0;
        while (i++ < 3) {
            Socket socket = serverSocket.accept();
            System.out.println("incoming connection: " + socket);
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String msg = br.readLine();
                System.out.println("echo server received: " + msg);
                msg = msg.toUpperCase();

                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                PrintWriter pw = new PrintWriter(osw);
                pw.println(msg);
                System.out.println("echo server sent: " + msg);
                pw.flush();
            } finally {
                socket.close();
            }
        }

        System.out.println("echo server finished");
        serverSocket.close();
    }
}