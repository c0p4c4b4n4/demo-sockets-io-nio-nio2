package nio2.client.future;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Nio2EchoFutureClient {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

        Future<Void> connectFuture = client.connect(new InetSocketAddress("localhost", 7000));
        connectFuture.get();

        String message = "hello";
        System.out.println("request to server:" + message);

        byte[] bytes = message.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        Future<Integer> writeFuture = client.write(buffer);
        writeFuture.get();

        buffer.flip();
        Future<Integer> readFuture = client.read(buffer);
        readFuture.get();

        String response = new String(buffer.array()).trim();
        buffer.clear();
        System.out.println("response from server: " + response);

        client.close();
    }
}