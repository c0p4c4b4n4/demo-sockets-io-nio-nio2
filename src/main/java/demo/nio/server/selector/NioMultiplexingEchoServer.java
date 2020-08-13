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

    private static boolean active = true;

    public static void main(String[] args) throws IOException {
        final int ports = 7;
        ServerSocketChannel[] serverSocketChannels = new ServerSocketChannel[ports];

        Selector selector = Selector.open();

        for (int p = 0; p < ports; p++) {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannels[p] = serverSocketChannel;
            serverSocketChannel.configureBlocking(false);

            serverSocketChannel.bind(new InetSocketAddress("localhost", 7000 + p));
            logger.info("echo server started: {}", serverSocketChannel);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }

        while (active) {
            if (selector.select() == 0) {
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

        for (ServerSocketChannel serverSocketChannel : serverSocketChannels) {
            serverSocketChannel.close();
        }
        logger.info("echo server finished");
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
        int read = socketChannel.read(buffer); // non-blocking
        logger.info("echo server read: {} byte(s)", read);

        buffer.flip();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        String message = new String(bytes, StandardCharsets.UTF_8);
        logger.info("echo server received: {}", message);
        if (message.trim().equals("bye")) {
            active = false;
        }

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
