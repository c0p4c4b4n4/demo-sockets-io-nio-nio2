package nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SelectorTimeBinServer2 {

    private static final int DEFAULT_PORT = 9999;

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);

        System.out.println("Server starting ... listening on port " + port);

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int numReadyChannels = selector.select();
            if (numReadyChannels == 0)
                continue; // there are no ready channels to process

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    // A connection was accepted by a ServerSocketChannel.
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    if (client == null) // in case accept() returns null
                        continue;

                    client.configureBlocking(false); // must be nonblocking
                    // Register socket channel with selector for read operations.
                    client.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    // A socket channel is ready for reading.
                    SocketChannel client = (SocketChannel) key.channel();
                    // Perform work on the socket channel.
                } else if (key.isWritable()) {
                    // A socket channel is ready for writing.
                    SocketChannel client = (SocketChannel) key.channel();
                    // Perform work on the socket channel.
                }
                keyIterator.remove();
            }
        }
    }
}