package demo.nio2.completion_handler.client;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Nio2EchoClientCompletionHandler2 extends Demo {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static void main(String[] args) throws IOException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();

        socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
        socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024);
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        Attachment attachment = new Attachment();
        attachment.messages = new String[]{"Alpha", "Bravo", "Charlie"};
        attachment.active = true;

        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(socketChannel, CHARSET);
        socketChannel.connect(new InetSocketAddress("localhost", 7000), attachment, acceptCompletionHandler);

        while (attachment.active) {
        }

        socketChannel.close();
        logger.info("echo client finished");
    }
}