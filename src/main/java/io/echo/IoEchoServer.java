package io.echo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IoEchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9001);
        ExecutorService executor = Executors.newCachedThreadPool();
        while (true) {
            Socket socket = server.accept();
            executor.submit(new Worker(socket));
        }
    }

    private static class Worker implements Runnable {

        private final Socket socket;

        Worker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();

                int read = 0;
                byte[] buf = new byte[1024];
                while ((read = in.read(buf)) != -1) {
                    out.write(buf, 0, read);
                }
            } catch (IOException e) {
                System.err.println(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
    }
}

