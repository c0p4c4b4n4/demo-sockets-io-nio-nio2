package demo.nio.server.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioSelectorEchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));
        System.out.println("echo server started: " + serverSocketChannel);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select(); // blocking

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keysIterator = keys.iterator();

            while (keysIterator.hasNext()) {
                SelectionKey key = keysIterator.next();

                if (key.isAcceptable()) {
                    System.out.println("accept");

                    ServerSocketChannel serverSocketChannel2 = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverSocketChannel2.accept();
                    if (socketChannel != null) {
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                }

                if (key.isReadable()) {
                    System.out.println("read");

                    keysIterator.remove();

                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    ByteBuffer inputBuffer = ByteBuffer.allocate(2048);
                    socketChannel.read(inputBuffer);

                    inputBuffer.flip();
                    byte[] bytes = new byte[inputBuffer.limit()];
                    inputBuffer.get(bytes);

                    System.out.println("Received message from client : " + new String(bytes));
                    inputBuffer.flip();

                    socketChannel.register(selector, SelectionKey.OP_WRITE, inputBuffer);
                }

                if (key.isWritable()) {
                    System.out.println("write");

                    keysIterator.remove();

                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer inputBuffer = (ByteBuffer) key.attachment();
                    socketChannel.write(inputBuffer);
                    socketChannel.close();
                }
            }
        }
    }

}
