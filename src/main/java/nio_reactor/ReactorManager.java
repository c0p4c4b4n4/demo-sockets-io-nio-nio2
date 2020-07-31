package nio_reactor;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class ReactorManager {

    private static final int SERVER_PORT = 9001;

    public void startReactor(int port) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress(port));
        server.configureBlocking(false);

        Reactor reactor = new Reactor();
        reactor.registerChannel(SelectionKey.OP_ACCEPT, server);
        reactor.registerEventHandler(SelectionKey.OP_ACCEPT, new AcceptEventHandler(reactor.getDemultiplexer()));
        reactor.registerEventHandler(SelectionKey.OP_READ, new ReadEventHandler(reactor.getDemultiplexer()));
        reactor.registerEventHandler(SelectionKey.OP_WRITE, new WriteEventHandler());

        reactor.run();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Server Started at port : " + SERVER_PORT);
        new ReactorManager().startReactor(SERVER_PORT);
    }
}
