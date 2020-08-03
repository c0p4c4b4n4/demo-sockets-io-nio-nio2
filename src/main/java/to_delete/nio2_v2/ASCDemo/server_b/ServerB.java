package to_delete.nio2_v2.ASCDemo.server_b;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerB {

    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();

        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 7000);
        serverChannel.bind(hostAddress);

        Attachment attachment = new Attachment();
        attachment.serverChannel = serverChannel;

        CompletionHandler<AsynchronousSocketChannel, Attachment> handler = new ConnectionHandler();
        serverChannel.accept(attachment, handler);

        System.in.read();
    }
}