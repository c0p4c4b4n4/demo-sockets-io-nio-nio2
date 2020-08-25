package demo.io.server;

import demo.common.Demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IoEchoServer extends Demo {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7000);
        logger.info("echo server started: {}", serverSocket);

        boolean active = true;
        while (active) {
            Socket socket = serverSocket.accept(); // blocking
            logger.info("connection accepted: {}", socket);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) { // blocking
                logger.info("echo server read: {} byte(s)", read);

                String message = new String(bytes, 0, read, StandardCharsets.UTF_8);
                logger.info("echo server received: {}", message);
                if (message.trim().equals("bye")) {
                    active = false;
                }

                os.write(bytes, 0, read); // blocking
            }

            socket.close();
            logger.info("connection closed");
        }

        serverSocket.close();
        logger.info("echo server finished");
    }
}
