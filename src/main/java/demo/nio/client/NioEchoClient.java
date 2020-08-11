package demo.nio.client;

import demo.common.Demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NioEchoClient extends Demo {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static void main(String[] args) throws IOException  {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String message;
        while ((message = in.readLine()) != null) {
            logger.info("echo client started");
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 7000));

            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            socketChannel.write(buffer);
            logger.info("echo client sent: " + message);

            int totalRead = 0;
            while (totalRead < message.getBytes().length) {
                buffer.clear();

                int read = socketChannel.read(buffer);
                if (read <= 0)
                    break;

                totalRead += read;

                buffer.flip();
                logger.info("echo client received: " + CHARSET.newDecoder().decode(buffer));
            }

            socketChannel.close();
            logger.info("echo client disconnected");
        }
    }
}
