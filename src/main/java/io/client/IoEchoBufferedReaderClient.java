package io.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IoEchoBufferedReaderClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] msgs = {"Alpha", "Bravo", "Charlie"};
        for (String msg : msgs) {
            Socket socket = new Socket("localhost", 7000);
            System.out.println("echo client connected: " + socket);

            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            PrintWriter pw = new PrintWriter(osw);
            pw.println(msg);
            pw.flush();

            System.out.println("echo client sent: " + msg);

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            System.out.println("echo client received: " + br.readLine());

            socket.close();
            System.out.println("echo client disconnected");
            Thread.sleep(1000);
        }
    }
}