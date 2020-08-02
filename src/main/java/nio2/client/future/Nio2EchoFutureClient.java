package nio2.client.future;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Nio2EchoFutureClient {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

        Future<Void> connectResult = client.connect(new InetSocketAddress("localhost", 9001));
        connectResult.get();

        String message = "hello";
        System.out.println("request to server:" + message);

        byte[] bytes = message.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        Future<Integer> writeResult = client.write(buffer);
        writeResult.get();

        buffer.flip();
        Future<Integer> readResult = client.read(buffer);
        readResult.get();

        String response = new String(buffer.array()).trim();
        buffer.clear();
        System.out.println("response from server: " + response);

        client.close();
    }
}