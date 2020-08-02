package nio2.b;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

public class ServerB {

    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();

        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9001);
        serverChannel.bind(hostAddress);

        while (true) {
            Attachment attachment = new Attachment();
            attachment.serverChannel = serverChannel;

            CompletionHandler<AsynchronousSocketChannel, Attachment> handler = new ConnectionHandler();
            serverChannel.accept(attachment, handler);

            System.in.read();
        }
    }
}