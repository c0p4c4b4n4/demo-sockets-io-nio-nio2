package demo.nio.server.channel;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NioNonBlockingEchoServer extends Demo {

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("echo server is starting...");

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        logger.info("is blocking: " + serverSocketChannel.isBlocking());

        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(7000));
        logger.info("echo server started: " + serverSocket);

        int i = 0;
        while (i < 3) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                i++;
                logger.info("incoming connection: " + socketChannel);

                ByteBuffer buffer = ByteBuffer.allocate(4);
                while (true) {
                    buffer.clear();
                    int n = socketChannel.read(buffer);
                    if (n <= 0) {
                        break;
                    }
                    buffer.flip();

                    sleep(1000);

                    socketChannel.write(buffer);
                    logger.info("echo server sent: " + buffer.asCharBuffer());
                }

                socketChannel.close();
            } else {
                logger.info("waiting for incoming connection...");
                sleep(1000);
            }
        }

        logger.info("echo server finished");
        serverSocket.close();
    }
}