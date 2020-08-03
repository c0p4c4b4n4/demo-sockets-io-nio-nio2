package nio.server.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NioNonBlockingEchoServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("echo server is starting...");

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        System.out.println("is blocking: " + serverSocketChannel.isBlocking());

        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(7000));
        System.out.println("echo server started: " + serverSocket);

        int i = 0;
        while (i < 3) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                i++;
                System.out.println("incoming connection: " + socketChannel);

                ByteBuffer buffer = ByteBuffer.allocate(10);
                while (true) {
                    buffer.clear();
                    int n = socketChannel.read(buffer);
                    if (n <= 0) {
                        break;
                    }
                    buffer.flip();

                    socketChannel.write(buffer);
                    System.out.println("echo server sent: " + buffer.asCharBuffer().toString());
                }

                socketChannel.close();
            } else {
                System.out.println("waiting for incoming connection...");
                Thread.sleep(1000);
            }
        }

        System.out.println("echo server finished");
        serverSocket.close();
    }
}