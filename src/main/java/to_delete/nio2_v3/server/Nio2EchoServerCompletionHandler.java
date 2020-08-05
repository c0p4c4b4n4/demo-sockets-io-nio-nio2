package to_delete.nio2_v3.server;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class Nio2EchoServerCompletionHandler extends Demo {

    public static void main(String[] args) throws IOException {
        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()) {
            if (!serverSocketChannel.isOpen()) {
                throw new IOException("Asynchronous server socket channel is not open !");
            }

            serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
            serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

            serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));
            logger.info("echo server started: " + serverSocketChannel);

            AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(serverSocketChannel);
            serverSocketChannel.accept(null, acceptCompletionHandler);

            System.in.read(); // wait
            logger.info("echo server finished");
        }
    }

    static class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {

        private final AsynchronousServerSocketChannel serverSocketChannel;
        private final ByteBuffer buffer;

        AcceptCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel) {
            this.serverSocketChannel = serverSocketChannel;
            this.buffer = ByteBuffer.allocate(1024);
        }

        @Override
        public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
            serverSocketChannel.accept(null, this);

            try {
                logger.info("incoming connection from: " + socketChannel.getRemoteAddress());

                while (socketChannel.read(buffer).get() != -1) { // blocked
                    buffer.flip();

                    socketChannel.write(buffer).get(); // blocked

                    if (buffer.hasRemaining()) {
                        buffer.compact();
                    } else {
                        buffer.clear();
                    }
                }
            } catch (IOException | InterruptedException | ExecutionException e) {
                logger.error("Exception during echo processing", e);
            } finally {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    logger.error("Exception during asynchronous socket channel close", e);
                }
            }
        }

        @Override
        public void failed(Throwable t, Void attachment) {
            logger.error("Exception during accept connection", t);
        }
    }
}
