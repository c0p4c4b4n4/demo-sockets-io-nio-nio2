package demo.nio2.completion_handler.client2;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class Client extends Demo {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(socketChannel);

        Attachment attachment = new Attachment();
        attachment.messages = new String[]{"Alpha", "Bravo", "Charlie"};
        attachment.active = true;
        socketChannel.connect(new InetSocketAddress("localhost", 7000), attachment, acceptCompletionHandler);

        while (attachment.active) {
        }

        socketChannel.close();
        logger.info("echo client finished");
    }
}