package nio2.ASCDemo.server;

import java.io.IOException;

import java.net.SocketAddress;

import java.nio.ByteBuffer;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    @Override
    public void completed(AsynchronousSocketChannel channelClient, Attachment attachment) {
        try {
            SocketAddress socketAddress = channelClient.getRemoteAddress();
            System.out.printf("Accepted connection from %s%n", socketAddress);

            attachment.serverSocketChannel.accept(attachment, this);

            Attachment attachment2 = new Attachment();
            attachment2.serverSocketChannel = attachment.serverSocketChannel;
            attachment2.socketChannel = channelClient;
            attachment2.isReadMode = true;
            attachment2.buffer = ByteBuffer.allocate(2048);
            attachment2.clientSocketAddress = socketAddress;

            ReadWriteHandler readWriteHandler = new ReadWriteHandler();
            channelClient.read(attachment2.buffer, attachment2, readWriteHandler);
        } catch (IOException e) {
            System.out.println("Failed to complete connection");
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        System.out.println("Failed to accept connection");
        t.printStackTrace();
    }
}