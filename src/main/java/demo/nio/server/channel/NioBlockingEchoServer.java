package demo.nio.server.channel;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class NioBlockingEchoServer extends Demo {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        logger.info("echo server is blocking: {}", serverSocketChannel.isBlocking());

        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress("localhost", 7000));
        logger.info("echo server started: {}", serverSocket);

        boolean active = true;
        while (active) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            logger.info("incoming connection accepted: {}", socketChannel);
            logger.info("incoming connection is blocking: {}", socketChannel.isBlocking());

            ByteBuffer buffer = ByteBuffer.allocate(4);
            while (true) {
                buffer.clear();
                int read = socketChannel.read(buffer);
                logger.info("echo server read: {} byte(s)", read);
                if (read <= 0) {
                    break;
                }

                buffer.flip();
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                String message = new String(bytes, StandardCharsets.UTF_8);
                logger.info("echo server received: {}", message);

                if (message.trim().equals("bye")) {
                    active = false;
                }

                sleep(1000);

                buffer.flip();
                socketChannel.write(buffer);
            }

            socketChannel.close();
            logger.info("incoming connection closed");
        }

        logger.info("echo server finished");
        serverSocket.close();
    }
}