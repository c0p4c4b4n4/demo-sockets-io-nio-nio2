package demo.nio2.completion_handler.client2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

public class Client {
    private final static Charset CSUTF8 = Charset.forName("UTF-8");

    private final static int PORT = 9090;

    private final static String HOST = "localhost";

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        AsynchronousSocketChannel   socketChannel = AsynchronousSocketChannel.open();

        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(socketChannel);

        Attachment attachment = new Attachment();
        attachment.socketChannel = socketChannel;
        attachment.messages = new String[] {"Alpha", "Bravo", "Charlie"};
        attachment.active = true;
        socketChannel.connect(new InetSocketAddress("localhost", 7000), attachment, acceptCompletionHandler);
/*
        socketChannel.connect(new InetSocketAddress("localhost", 7000)).get();
        System.out.printf("Client at %s connected%n", socketChannel.getLocalAddress());

        Attachment att = new Attachment();
        att.socketChannel = socketChannel;
        att.isReadMode = false;
        att.buffer = ByteBuffer.allocate(2048);
        att.mainThd = Thread.currentThread();

        byte[] data = "Hello".getBytes(CSUTF8);
        att.buffer.put(data);
        att.buffer.flip();
        socketChannel.write(att.buffer, att, new ReadCompletionHandler());
*/
        while (attachment.active) {
        }

        socketChannel.close();
        System.out.println("echo client finished");
    }
}