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

public class AsyncEchoServer2 {
    private AsynchronousServerSocketChannel serverChannel;
    private AsynchronousSocketChannel clientChannel;

    public AsyncEchoServer2() {
        try {
            serverChannel = AsynchronousServerSocketChannel.open();
            InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9001);
            serverChannel.bind(hostAddress);
            while (true) {

                CompletionHandler<AsynchronousSocketChannel, Attachment> handler = new ConnectionHandler();
                serverChannel.accept(null, handler);
                try {
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AsyncEchoServer2();
    }

    private class
}