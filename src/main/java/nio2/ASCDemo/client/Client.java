package nio2.ASCDemo.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

public class Client {
    private final static Charset CSUTF8 = Charset.forName("UTF-8");

    private final static int PORT = 9090;

    private final static String HOST = "localhost";

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        AsynchronousSocketChannel channel;
        channel = AsynchronousSocketChannel.open();

        channel.connect(new InetSocketAddress("localhost", 7000)).get();
        System.out.printf("Client at %s connected%n", channel.getLocalAddress());

        Attachment att = new Attachment();
        att.channel = channel;
        att.isReadMode = false;
        att.buffer = ByteBuffer.allocate(2048);
        att.mainThd = Thread.currentThread();

        byte[] data = "Hello".getBytes(CSUTF8);
        att.buffer.put(data);
        att.buffer.flip();
        channel.write(att.buffer, att, new ReadCompletionHandler());

        Thread.currentThread().join();
    }
}