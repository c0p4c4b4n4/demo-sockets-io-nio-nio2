package demo.patterns.proactor.echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class ProactorInitiator {

    private static final int SERVER_PORT = 7000;

    public void initiateProactiveServer(int port) throws IOException {
        AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(listener);

        SessionState state = new SessionState();
        listener.accept(state, acceptCompletionHandler);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting Proactor NIO2 echo server at port: " + SERVER_PORT);
        new ProactorInitiator().initiateProactiveServer(SERVER_PORT);

        // sleep indefinitely since otherwise the JVM would terminate
        while (true) {
            Thread.sleep(Long.MAX_VALUE);
        }
    }
}


