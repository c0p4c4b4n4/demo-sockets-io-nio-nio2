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

    private static final AtomicBoolean active = new AtomicBoolean(true);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7000);
        logger.info("echo server started: {}", serverSocket);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        while (active.get()) {
            Socket socket = serverSocket.accept(); // blocking
            executorService.submit(new Worker(socket));
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

        Worker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                logger.info("connection accepted: {}", socket);

                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                int read;
                byte[] bytes = new byte[4];
                while ((read = is.read(bytes)) != -1) { // blocking
                    logger.info("echo server read: {} byte(s)", read);

                    String message = new String(bytes, 0, read, StandardCharsets.UTF_8);
                    logger.info("echo server received: {}", message);
                    if (message.trim().equals("bye")) {
                        active.set(false);
                    }

                    os.write(bytes, 0, read); // blocking
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
