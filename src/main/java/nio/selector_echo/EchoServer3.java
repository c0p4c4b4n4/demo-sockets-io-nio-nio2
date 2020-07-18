package nio.selector_echo;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoServer3 {

    private static final ByteBuffer buffer = ByteBuffer.allocate(1024);

    static public void main(String[] args) throws Exception {
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ServerSocket ss = ssc.socket();
        InetSocketAddress address = new InetSocketAddress("localhost", 9999);
        ss.bind(address);

        SelectionKey key0 = ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int num = selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();

                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                    // Accept the new connection
                    ServerSocketChannel ssc2 = (ServerSocketChannel) key.channel();
                    SocketChannel sc = ssc2.accept();
                    sc.configureBlocking(false);

                    // Add the new connection to the selector
                    SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
                    it.remove();

                    System.out.println("Got connection from " + sc);
                } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    // Read the data
                    SocketChannel sc = (SocketChannel) key.channel();

                    while (true) {
                        buffer.clear();
                        int n = sc.read(buffer);

                        if (n <= 0) {
                            break;
                        }

                        buffer.flip();
                        sc.write(buffer);
                    }

                    it.remove();
                }
            }

            //selectedKeys.clear();
        }
    }
}
