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
        logger.info("echo server started: {}", serverSocket);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        AtomicBoolean active = new AtomicBoolean(true);
        while (active.get()) {
            Socket socket = serverSocket.accept(); // blocking
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
                logger.info("connection accepted: {}", socket);

                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                int n;
                byte[] bytes = new byte[4];
                while ((n = is.read(bytes)) != -1) { // blocking
                    logger.info("echo server read: {} byte(s)", n);

                    String message = new String(bytes, 0, n, StandardCharsets.UTF_8);
                    logger.info("echo server received: {}", message);

                    if (message.trim().equals("bye")) {
                        active.set(false);
                    }

                    sleep(1000);

                    os.write(bytes, 0, n); // blocking
                }
            } catch (IOException e) {
                logger.error("exception during socket reading/writing", e);
            } finally {
                try {
                    socket.close();
                    logger.info("connection closed");
                } catch (IOException e) {
                    logger.error("exception during socket closing", e);
                }
            }
        }
    }
}

