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
            Socket socket = serverSocket.accept();
            logger.info("connection accepted: {}", socket);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            int n;
            byte[] bytes = new byte[4];
            while ((n = is.read(bytes)) != -1) {
                logger.info("echo server read: {} byte(s)", n);

                String message = new String(bytes, 0, n, StandardCharsets.UTF_8);
                logger.info("echo server received: {}", message);

                if (message.trim().equals("bye")) {
                    active = false;
                }

                sleep(1000);

                os.write(bytes, 0, n);
            }

            socket.close();
            logger.info("connection closed");
        }

        serverSocket.close();
        logger.info("echo server finished");
    }
}

