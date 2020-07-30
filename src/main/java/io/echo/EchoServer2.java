package io.echo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

public class EchoServer2 {

    private static final int PORT = 4000;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                Socket socket = server.accept();
                executor.submit(new Handler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler implements Runnable {

        private final Socket socket;

        Handler(Socket s) {
            socket = s;
        }

        @Override
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                int read = 0;
                byte[] buf = new byte[BUFFER_SIZE];
                while ((read = in.read(buf)) != -1) {
                    out.write(buf, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

