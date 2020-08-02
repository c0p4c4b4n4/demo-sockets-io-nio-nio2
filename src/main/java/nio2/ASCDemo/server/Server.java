package nio2.ASCDemo.server;

import java.io.IOException;

import java.net.InetSocketAddress;

import java.nio.channels.AsynchronousServerSocketChannel;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 9001));

        System.out.println("echo server started: " + serverSocketChannel);

        Attachment attachment = new Attachment();
        attachment.serverSocketChannel = serverSocketChannel;
        serverSocketChannel.accept(attachment, new ConnectionHandler());

        Thread.currentThread().join();
    }
}