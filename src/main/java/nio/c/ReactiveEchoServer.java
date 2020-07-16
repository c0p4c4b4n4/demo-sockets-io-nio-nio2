package nio.c;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

class ReactiveEchoServer  {

    public static void main(String[] args) throws IOException{
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.socket().bind(new InetSocketAddress(9999));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();

            while (keysIterator.hasNext()) {
                SelectionKey key = keysIterator.next();
                keysIterator.remove();

                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    new Handler(selector, socketChannel);
                }
            }
        }
    }
}
