package demo.nio2.future.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.*;

public class Nio2EchoFutureServerThreadPool {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        ExecutorService taskExecutor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();

        serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

        serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));
        System.out.println("echo server started: " + serverSocketChannel);

        int i = 0;
        while (i++ < 3) {
            Future<AsynchronousSocketChannel> socketChannelFuture = serverSocketChannel.accept();

            AsynchronousSocketChannel socketChannel = socketChannelFuture.get();
            System.out.println("incoming connection: " + socketChannel);

            Callable<String> worker = new Worker(socketChannel);
            taskExecutor.submit(worker);
        }

        serverSocketChannel.close();
        System.out.println("echo server is finishing");

        taskExecutor.shutdown();
        while (!taskExecutor.isTerminated()) {
        }

        System.out.println("echo server finished");
    }

    private static class Worker implements Callable<String> {

        private final AsynchronousSocketChannel socketChannel;

        public Worker(AsynchronousSocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public String call() throws Exception {
            System.out.println("incoming connection: " + socketChannel);

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
            System.out.println("incoming connection finished");

            return "???";
        }
    }
}