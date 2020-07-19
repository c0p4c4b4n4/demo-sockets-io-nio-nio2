package nio.selector_echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioEchoServer3 {

    public static void main(String[] args) throws IOException {
        System.out.println("echo server is starting...");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress("localhost", 9001));
        System.out.println("echo server started: " + serverSocketChannel);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        int i = 0;
        while (i < 3) {
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (key.isAcceptable()) {
                    i++;

                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("echo client connected: " + socketChannel);
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }

                if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(10);

                    while (true) {
                        buffer.clear();
                        int n = socketChannel.read(buffer);
                        System.out.println("read bytes: " + n);

                        if (n <= 0) {
                            socketChannel.close();
                            System.out.println("echo client disconnected");
                            break;
                        }

                        buffer.flip();
                        socketChannel.write(buffer);
                    }
                }
            }
        }

        System.out.println("echo server finished");
        serverSocketChannel.close();
    }
}
