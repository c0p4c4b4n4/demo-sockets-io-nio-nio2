package demo.nio2.future.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Nio2EchoFutureClient {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        String[] msgs = {"Alpha", "Bravo", "Charlie"};
        for (String msg : msgs) {
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();

            Future<Void> connectFuture = socketChannel.connect(new InetSocketAddress("localhost", 7000));
            connectFuture.get(); // blocked

            System.out.println("echo client sent: " + msg);

            byte[] bytes = msg.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            Future<Integer> writeFuture = socketChannel.write(buffer);
            writeFuture.get();  // blocked

            buffer.flip();
            Future<Integer> readFuture = socketChannel.read(buffer);
            readFuture.get();  // blocked

            String response = new String(buffer.array()).trim();
            buffer.clear();
            System.out.println("echo client received: " + response);

            socketChannel.close();
        }
    }
}