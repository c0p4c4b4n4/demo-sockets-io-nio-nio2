package nio.c;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

class ReactiveEchoServer  {

    private static class Acceptor implements Runnable {

        private final ServerSocketChannel serverSocketChannel;
        private final Selector selector;

         Acceptor(ServerSocketChannel serverSocketChannel, Selector selector) {
            this.serverSocketChannel = serverSocketChannel;
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    new Handler(selector, socketChannel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException{
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.socket().bind(new InetSocketAddress(9999));

        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor(serverSocketChannel, selector));

        while (true) {
            selector.select();
            Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();

            while (keysIterator.hasNext()) {
                SelectionKey key = (SelectionKey) keysIterator.next();
                keysIterator.remove();

                Runnable runnable = (Runnable) key.attachment();
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }
}
