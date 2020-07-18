package io.echo;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class EchoClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9001);
        String msg = "abcdefghijklmnopqrstuvwxyz";

        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        PrintWriter pw = new PrintWriter(osw);
        pw.println(msg);
        System.out.println("client sent: " + msg);
        pw.flush();

        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        System.out.println("client received: " + br.readLine());
    }
}