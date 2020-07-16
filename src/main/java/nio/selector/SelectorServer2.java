package nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SelectorServer2 {

    private static final  int DEFAULT_PORT = 9999;

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
            int readyChannelsNumber = selector.select();
            if (readyChannelsNumber == 0)
                continue;

            Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
            while (keysIterator.hasNext()) {
                SelectionKey key = (SelectionKey) keysIterator.next();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                    if (socketChannel == null)
                        continue;

                    System.out.println("Receiving connection");

                    ByteBuffer bb = ByteBuffer.allocateDirect(8);
                    bb.clear();
                    bb.putLong(System.currentTimeMillis());
                    bb.flip();

                    System.out.println("Writing current time");
                    while (bb.hasRemaining())
                        socketChannel.write(bb);

                    socketChannel.close();
                }
                keysIterator.remove();
            }
        }
    }
}