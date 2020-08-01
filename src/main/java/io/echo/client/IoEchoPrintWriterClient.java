package io.echo.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IoEchoPrintWriterClient {

    public static void main(String[] args) throws IOException {
        System.out.println("echo client is starting");
        Socket socket = new Socket("localhost", 9001);
        System.out.println("echo client started: " + socket);

        String msg = "abcdefghijklmnopqrstuvwxyz";

        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        PrintWriter pw = new PrintWriter(osw);
        pw.println(msg);
        System.out.println("echo client sent: " + msg);
        pw.flush();

        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        System.out.println("echo client received: " + br.readLine());

        System.out.println("echo client finished");
    }
}