package patterns.proactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ProactorInitiator {

    private static final int SERVER_PORT = 9001;

    public void initiateProactiveServer(int port) throws IOException {
        AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(listener);

        SessionState state = new SessionState();
        listener.accept(state, acceptCompletionHandler);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting NIO2 server at port: " + SERVER_PORT);
        new ProactorInitiator().initiateProactiveServer(SERVER_PORT);

        // Sleep indefinitely since otherwise the JVM would terminate
        while (true) {
            Thread.sleep(Long.MAX_VALUE);
        }
    }
}


