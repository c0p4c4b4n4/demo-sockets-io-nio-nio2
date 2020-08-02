package nio2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Nio2EchoClient {

    public static void main(String[] args) throws Exception {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9001);
        Future<Void> future = client.connect(hostAddress);
        future.get();

        String message = "abc";

        byte[] bytes = message.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Future<Integer> writeResult = client.write(buffer);
        writeResult.get();

        buffer.flip();
        Future<Integer> readResult = client.read(buffer);
        readResult.get();

        String response = new String(buffer.array()).trim();
        System.out.println("response from server: " + response);
        buffer.clear();

        client.close();
    }
}
