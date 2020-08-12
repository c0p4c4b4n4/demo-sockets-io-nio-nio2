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

public class NioSelector2EchoServer extends Demo {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));
        logger.info("echo server started: " + serverSocketChannel);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        boolean active = true;
        while (active) {
            selector.select(); // blocking

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keysIterator = keys.iterator();

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

        serverSocketChannel.close();
        logger.info("echo server finished");
    }

    private static void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (socketChannel != null) {
            logger.info("connection is accepted: " + socketChannel);

            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
    }

    private static void read(Selector selector, SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
        int n = socketChannel.read(inputBuffer);
        logger.info("server read: " + n);

        inputBuffer.flip();
        byte[] bytes = new byte[inputBuffer.limit()];
        inputBuffer.get(bytes);

        logger.info("echo server received: " + new String(bytes, StandardCharsets.UTF_8));
        inputBuffer.flip();

        socketChannel.register(selector, SelectionKey.OP_WRITE, inputBuffer);
    }

    private static void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer inputBuffer = (ByteBuffer) key.attachment();
        socketChannel.write(inputBuffer);
        socketChannel.close();
    }
}
