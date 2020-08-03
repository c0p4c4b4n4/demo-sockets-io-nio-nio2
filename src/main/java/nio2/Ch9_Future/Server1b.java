package nio2.Ch9_Future;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.*;

public class Server1b {

    public static void main(String[] args) throws IOException {
        ExecutorService taskExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

        //create asynchronous server-socket channel bound to the default group
        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()) {

            if (serverSocketChannel.isOpen()) {
                serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));

                System.out.println("Waiting for connections ...");
                while (true) {
                    Future<AsynchronousSocketChannel> socketChannelFuture = serverSocketChannel.accept();

                    try {
                        AsynchronousSocketChannel socketChannel = socketChannelFuture.get();
                        Callable<String> worker = new Callable<String>() {

                            @Override
                            public String call() throws Exception {
                                String host = socketChannel.getRemoteAddress().toString();
                                System.out.println("Incoming connection from: " + host);

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

                                socketChannel.close();
                                System.out.println(host + " was successfully served!");
                                return host;
                            }
                        };

                        taskExecutor.submit(worker);

                    } catch (InterruptedException | ExecutionException ex) {
                        System.err.println(ex);

                        System.err.println("\n Server is shutting down ...");

                        //this will make the executor accept no new threads and finish all existing threads in the queue
                        taskExecutor.shutdown();

                        //wait until all threads are finish                        
                        while (!taskExecutor.isTerminated()) {
                        }

                        break;
                    }
                }
            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        }
    }
}
