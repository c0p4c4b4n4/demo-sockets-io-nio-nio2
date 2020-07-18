package nio.channel_time;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;

public class TimeNonBlockingServer {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server...");

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        System.out.println("is blocking: " + serverSocketChannel.isBlocking());

        serverSocketChannel.socket().bind(new InetSocketAddress(9002));

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                System.out.println("received connection from " + socketChannel.socket().getRemoteSocketAddress());

                String msg = LocalDateTime.now().toString();
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                buffer.rewind();

                socketChannel.write(buffer);
                socketChannel.close();
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }
        }
    }
}