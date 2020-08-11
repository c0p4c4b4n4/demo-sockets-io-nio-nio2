package demo.io.server;

import demo.common.Demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class IoEchoThreadPoolServer extends Demo {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7000);
        logger.info("echo server started: " + serverSocket);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        AtomicBoolean active = new AtomicBoolean(true);
        while (active.get()) {
            Socket socket = serverSocket.accept();
            executorService.submit(new Worker(socket, active));
        }

        logger.info("echo server is finishing");
        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }

        serverSocket.close();
        logger.info("echo server finished");
    }

    private static class Worker implements Runnable {

        private final Socket socket;
        private final AtomicBoolean active;

        Worker(Socket socket, AtomicBoolean active) {
            this.socket = socket;
            this.active = active;
        }

        @Override
        public void run() {
            try {
                logger.info("incoming connection accepted: " + socket);

                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                int read;
                byte[] bytes = new byte[4];
                while ((read = is.read(bytes)) != -1) {
                    logger.info("server read: " + read);

                    String message = new String(bytes, 0, read, StandardCharsets.UTF_8);
                    logger.info("server received: " + message);

                    if (message.trim().equals("bye")) {
                        active.set(false);
                    }

                    sleep(1000);
                    os.write(bytes, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    logger.info("incoming connection closed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

