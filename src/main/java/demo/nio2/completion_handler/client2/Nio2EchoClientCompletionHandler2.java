package demo.nio2.completion_handler.client2;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class Nio2EchoClientCompletionHandler2 extends Demo {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();

        Attachment attachment = new Attachment();
        attachment.messages = new String[]{"Alpha", "Bravo", "Charlie"};
        attachment.active = true;

        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(socketChannel,CHARSET);
        socketChannel.connect(new InetSocketAddress("localhost", 7000), attachment, acceptCompletionHandler);

        while (attachment.active) {
        }

        socketChannel.close();
        logger.info("echo client finished");
    }
}