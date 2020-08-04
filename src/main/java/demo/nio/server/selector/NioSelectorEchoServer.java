package demo.nio.server.selector;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioSelectorEchoServer extends Demo {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));
        logger.info("echo server started: " + serverSocketChannel);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select(); // blocking

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keysIterator = keys.iterator();

            while (keysIterator.hasNext()) {
                SelectionKey key = keysIterator.next();

                if (key.isAcceptable()) {
                    logger.info("key is acceptable: " + key);

                    ServerSocketChannel serverSocketChannel2 = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel2.accept();
                    if (socketChannel != null) {
                        logger.info("connection is accepted: " + socketChannel);

                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                }

                if (key.isReadable()) {
                    keysIterator.remove();
                    logger.info("key is readable: " + key);

                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
                    socketChannel.read(inputBuffer);

                    inputBuffer.flip();
                    byte[] bytes = new byte[inputBuffer.limit()];
                    inputBuffer.get(bytes);

                    logger.info("echo server received: " + new String(bytes));
                    inputBuffer.flip();

                    socketChannel.register(selector, SelectionKey.OP_WRITE, inputBuffer);
                }

                if (key.isWritable()) {
                    keysIterator.remove();
                    logger.info("key is writable: " + key);

                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer inputBuffer = (ByteBuffer) key.attachment();
                    socketChannel.write(inputBuffer);
                    socketChannel.close();
                }
            }
        }

//        serverSocketChannel.close();
//        logger.info("echo server finished");
    }
}
