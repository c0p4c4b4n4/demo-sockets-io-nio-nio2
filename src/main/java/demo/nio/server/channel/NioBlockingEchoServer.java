package demo.nio.server.channel;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class NioBlockingEchoServer extends Demo {

    private static final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        logger.info("echo server is blocking: {}", serverSocketChannel.isBlocking());

        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress("localhost", 7000));
        logger.info("echo server started: {}", serverSocket);

        boolean active = true;
        while (active) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            logger.info("incoming connection: {}", socketChannel);

            ByteBuffer buffer = ByteBuffer.allocate(4);
            while (true) {
                buffer.clear();
                int read = socketChannel.read(buffer);
                logger.info("echo server read: {} byte(s)", read);
                if (read <= 0) {
                    break;
                }
                buffer.flip();

                sleep(1000);
                socketChannel.write(buffer);

                logger.info("echo server sent: {}", buffer.asCharBuffer());
            }

            socketChannel.close();
        }

        logger.info("echo server finished");
        serverSocket.close();
    }
}