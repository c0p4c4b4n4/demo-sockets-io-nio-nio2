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

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] msgs = {"Alpha", "Bravo", "Charlie"};
        for (String msg : msgs) {
            Socket socket = new Socket("localhost", 9001);
            System.out.println("echo client started: " + socket);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            byte[] bytes = msg.getBytes();
            os.write(bytes);

            int totalRead = 0;
            int read;
            while (totalRead < bytes.length) {
                if ((read = is.read(bytes, totalRead, bytes.length - totalRead)) == -1)
                    throw new SocketException("Connection closed prematurely");
                totalRead += read;
            }

            System.out.println("echo client received: " + new String(bytes));

            socket.close();
            System.out.println("echo client disconnected");
            Thread.sleep(1000);
        }
    }
}