package demo.nio.server.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioSelectorEchoServer {

    private static final int SERVER_PORT = 7000;

    public static void main(String[] args) throws IOException {
        System.out.println("Starting Reactor NIO echo server at port: " + SERVER_PORT);
        new NioSelectorEchoServer().initiateReactiveServer(SERVER_PORT);
    }

    public void initiateReactiveServer(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();

            Set<SelectionKey> readyHandles = selector.selectedKeys();
            Iterator<SelectionKey> handleIterator = readyHandles.iterator();

            while (handleIterator.hasNext()) {
                SelectionKey handle = handleIterator.next();

                if (handle.isAcceptable()) {
                    ReadEventHandler handler = new AcceptEventHandler(selector);
                    handler.handleEvent(handle);
                }

                if (handle.isReadable()) {
                    ReadEventHandler handler = new ReadEventHandler(selector);
                    handler.handleEvent(handle);
                    handleIterator.remove();
                }

                if (handle.isWritable()) {
                    ReadEventHandler handler = new WriteEventHandler(selector);
                    handler.handleEvent(handle);
                    handleIterator.remove();
                }
            }
        }
    }
}
