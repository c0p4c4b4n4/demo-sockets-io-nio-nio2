package nio2.Ch10_CompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server2b {

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
        AsynchronousChannelGroup threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);

        //create asynchronous server-socket channel bound to the default group
        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(threadGroup)) {

            if (serverSocketChannel.isOpen()) {
                serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));

                System.out.println("Waiting for connections ...");

                CompletionHandler<AsynchronousSocketChannel, Void> handler = new CompletionHandler2(serverSocketChannel);
                serverSocketChannel.accept(null, handler);

                System.in.read(); // wait
            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        }
    }

    private static class CompletionHandler2 implements CompletionHandler<AsynchronousSocketChannel, Void> {

        private final AsynchronousServerSocketChannel serverSocketChannel;
        private final ByteBuffer buffer;

        public CompletionHandler2(AsynchronousServerSocketChannel serverSocketChannel) {
            this.serverSocketChannel = serverSocketChannel;
            this.buffer = ByteBuffer.allocateDirect(1024);
        }

        @Override
        public void completed(AsynchronousSocketChannel result, Void attachment) {

            serverSocketChannel.accept(null, this);

            try {
                System.out.println("Incoming connection from: " + result.getRemoteAddress());

                while (result.read(buffer).get() != -1) {
                    buffer.flip();

                    result.write(buffer).get();

                    if (buffer.hasRemaining()) {
                        buffer.compact();
                    } else {
                        buffer.clear();
                    }
                }
            } catch (IOException | InterruptedException | ExecutionException ex) {
                System.err.println(ex);
            } finally {
                try {
                    result.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            serverSocketChannel.accept(null, this);
            throw new UnsupportedOperationException("Cannot accept connections!");
        }
    }
}
