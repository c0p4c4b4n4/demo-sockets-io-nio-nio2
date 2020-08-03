package demo.nio2.future.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Nio2EchoFutureServer {

    public static void main(String[] args) throws IOException {
        // create asynchronous server-socket channel bound to the default group
        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()) {

            if (serverSocketChannel.isOpen()) {
                serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));

                System.out.println("waiting for connections ...");
                while (true) {
                    Future<AsynchronousSocketChannel> socketChannelFuture = serverSocketChannel.accept();

                    try (AsynchronousSocketChannel socketChannel = socketChannelFuture.get()) {
                        System.out.println("incoming connection: " + socketChannel.getRemoteAddress());

                        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

                        while (socketChannel.read(buffer).get() != -1) {
                            buffer.flip();

                            socketChannel.write(buffer).get();
                            if (buffer.hasRemaining()) {
                                buffer.compact();
                            } else {
                                buffer.clear();
                            }
                        }

                        System.out.println(socketChannel.getRemoteAddress() + " was successfully served!");

                    } catch (IOException | InterruptedException | ExecutionException ex) {
                        System.err.println(ex);
                    }
                }

                //serverSocketChannel.close();
            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        }
    }
}
