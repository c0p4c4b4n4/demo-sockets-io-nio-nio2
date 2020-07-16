package nio.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ChannelServer {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server...");

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.socket().bind(new InetSocketAddress(9999));

        String msg = "local address: " + serverSocketChannel.socket().getLocalSocketAddress();
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                System.out.println("received connection from " + socketChannel.socket().getRemoteSocketAddress());

                buffer.rewind();

                socketChannel.write(buffer);
                socketChannel.close();
            } else
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    System.err.println(ie);
                }
        }
    }
}