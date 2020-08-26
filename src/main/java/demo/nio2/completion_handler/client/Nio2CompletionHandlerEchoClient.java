package demo.nio2.completion_handler.client;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Nio2CompletionHandlerEchoClient extends Demo {

    public static void main(String[] args) throws IOException {
        String[] messages = {"Alpha", "Bravo", "Charlie"};
        for (String message : messages) {
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();

            socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
            socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

            Attachment attachment = new Attachment(message, true);
            AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(socketChannel);
            socketChannel.connect(new InetSocketAddress("localhost", 7000), attachment, acceptCompletionHandler);

            while (attachment.getActive().get()) {
            }

            socketChannel.close();
            logger.info("Echo client finished");
        }
    }
}