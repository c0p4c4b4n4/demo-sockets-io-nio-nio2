package to_delete.io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IoEchoBufferedReaderServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7000);
        System.out.println("echo server started: " + serverSocket);

        int i = 0;
        while (i++ < 3) {
            Socket socket = serverSocket.accept();
            System.out.println("connection: " + socket);

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
            pw.flush();

            System.out.println("echo server sent: " + msg);
            socket.close();
        }

        serverSocket.close();
        System.out.println("echo server finished");
    }
}