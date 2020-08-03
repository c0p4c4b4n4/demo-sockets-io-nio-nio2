package demo.io.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IoEchoThreadPoolServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7000);
        System.out.println("echo server started: " + serverSocket);

        ExecutorService executorService = Executors.newCachedThreadPool();

        int i = 0;
        while (i++ < 3) {
            Socket socket = serverSocket.accept();
            executorService.submit(new Worker(socket));
        }

        System.out.println("echo server is finishing");
        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }

        serverSocket.close();
        System.out.println("echo server finished");
    }

    private static class Worker implements Runnable {

        private final Socket socket;

        Worker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                System.out.println("incoming connection: " + socket);

                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                int read;
                byte[] bytes = new byte[1024];
                while ((read = is.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    System.out.println("incoming connection finished");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

