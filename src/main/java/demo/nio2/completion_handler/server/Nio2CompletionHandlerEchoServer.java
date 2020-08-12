package demo.nio2.completion_handler.server;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class Nio2CompletionHandlerEchoServer extends Demo {

    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(7000));
        logger.info("echo server started");

        Attachment attachment = new Attachment();
        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(serverSocketChannel);
        serverSocketChannel.accept(attachment, acceptCompletionHandler);

        System.in.read(); // wait
        logger.info("echo server finished");
    }
}


