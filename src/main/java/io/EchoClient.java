package io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.UnknownHostException;

public class EchoClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9999);
        String msg = "hello";

        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        PrintWriter pw = new PrintWriter(osw);
        pw.println(msg);
        System.out.println("client sent: " + msg);
        pw.flush();

        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        System.out.println("client received: " + br.readLine());
    }
}