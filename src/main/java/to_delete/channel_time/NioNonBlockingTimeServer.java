package to_delete.channel_time;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;

public class NioNonBlockingTimeServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("time server is starting...");

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        System.out.println("is blocking: " + serverSocketChannel.isBlocking());

        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(9002));
        System.out.println("time server started: " + serverSocket);

        int i = 0;
        while (i < 3) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                i++;
                System.out.println("incoming connection: " + socketChannel);

                String msg = LocalDateTime.now().toString() + "\r\n";
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                buffer.rewind();

                socketChannel.write(buffer);
                socketChannel.close();
            } else {
                System.out.println("waiting for incoming connection...");
                Thread.sleep(1000);
            }
        }

        System.out.println("time server finished");
        serverSocket.close();
    }
}