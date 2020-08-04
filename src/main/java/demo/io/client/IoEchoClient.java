package demo.io.client;

import demo.common.Demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class IoEchoClient extends Demo {

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] messages = {"Alpha", "Bravo", "Charlie"};
        for (String message : messages) {
            Socket socket = new Socket("localhost", 7000);
            logger.info("echo client started: " + socket);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            byte[] bytes = message.getBytes();
            os.write(bytes);

            int totalRead = 0;
            while (totalRead < bytes.length) {
                int read = is.read(bytes, totalRead, bytes.length - totalRead);
                if (read <= 0)
                    break;

                totalRead += read;
                logger.info("echo client read: {}", read);
            }

            logger.info("echo client received: " + new String(bytes));

            socket.close();
            logger.info("echo client disconnected");
            Thread.sleep(1000);
        }
    }
}