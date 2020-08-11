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

public class NioSelector1EchoServer {

    public static void main(String[] args) throws IOException {
        System.out.println("echo server is starting...");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));
        System.out.println("echo server started: " + serverSocketChannel);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        boolean active = true;
        while (active) {
            selector.select();

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = keys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (key.isAcceptable()) {
                    accept(serverSocketChannel, selector);
                }

                if (key.isReadable()) {
                    read(key);
                }
            }
        }

        System.out.println("echo server finished");
        serverSocketChannel.close();
    }

    private static void accept(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        System.out.println("echo client connected: " + socketChannel);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private static void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(10);
        int read = socketChannel.read(buffer);
        if (read < 0) {
            socketChannel.close();
            System.out.println("echo client disconnected");
        } else {
            System.out.println("echo server received: " + new String(buffer.array()));

            buffer.flip();
            socketChannel.write(buffer);
        }
    }
}
