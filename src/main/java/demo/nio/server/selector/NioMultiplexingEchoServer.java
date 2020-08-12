package demo.nio.server.selector;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NioMultiplexingEchoServer extends Demo {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        for (int port = 7000; port <= 7007; port++) {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            serverSocketChannel.bind(new InetSocketAddress("localhost", port));
            logger.info("echo server started: {}", serverSocketChannel);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }

        boolean active = true;
        while (active) {
            int k = selector.select(); // blocking
            if (k == 0) {
                continue;
            }

            Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();

            while (keysIterator.hasNext()) {
                SelectionKey key = keysIterator.next();

                if (key.isAcceptable()) {
                    accept(selector, key);
                }

                if (key.isReadable()) {
                    keysIterator.remove();
                    read(selector, key);
                }

                if (key.isWritable()) {
                    keysIterator.remove();
                    write(key);
                }
            }
        }

//        serverSocketChannel.close();
//        logger.info("echo server finished");
    }

    private static void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null) {
            logger.info("connection is accepted: {}", socketChannel);

            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
    }

    private static void read(Selector selector, SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(4);
        int n = socketChannel.read(buffer); // non-blocking
        logger.info("echo server read: {} byte(s)", n);

        buffer.flip();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        String message = new String(bytes, StandardCharsets.UTF_8);
        logger.info("echo server received: {}", message);

        buffer.flip();
        socketChannel.register(selector, SelectionKey.OP_WRITE, buffer);
    }

    private static void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        socketChannel.write(buffer); // non-blocking
        socketChannel.close();
    }
}
