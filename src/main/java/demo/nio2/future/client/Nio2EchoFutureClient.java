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
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

            Future<Void> connectFuture = client.connect(new InetSocketAddress("localhost", 7000));
            connectFuture.get(); // blocked

            System.out.println("echo client sent: " + msg);

            byte[] bytes = msg.getBytes();
            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            Future<Integer> writeFuture = client.write(buffer);
            writeFuture.get();  // blocked

            buffer.flip();
            Future<Integer> readFuture = client.read(buffer);
            readFuture.get();  // blocked

            String response = new String(buffer.array()).trim();
            buffer.clear();
            System.out.println("echo client received: " + response);

            client.close();
        }
    }
}