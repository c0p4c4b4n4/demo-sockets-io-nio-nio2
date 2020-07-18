package io.echo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class EchoServer {

    public static void main(String[] args) throws IOException {
        System.out.println("echo server started...");

        ServerSocket serverSocket = new ServerSocket(9001);

        while (true) {
            Socket socket = serverSocket.accept();
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String msg = br.readLine();
                System.out.println("server received: " + msg);

                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                PrintWriter pw = new PrintWriter(osw);
                pw.println(msg);
                System.out.println("server sent: " + msg);
                pw.flush();
            } finally {
                socket.close();
            }
        }
    }
}