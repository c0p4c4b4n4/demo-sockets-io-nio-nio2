package nio.server.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class NioBlockingEchoServer {

    private static final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        System.out.println("server is blocking: " + serverSocketChannel.isBlocking());

        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress("localhost", 9001));
        System.out.println("time server started: " + serverSocket);

        int i = 0;
        while (i++ < 3) {
            SocketChannel socketChannel = serverSocketChannel.accept();
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
                System.out.println("time server sent: " + buffer.asCharBuffer().toString());
            }

            socketChannel.close();
        }

        System.out.println("time server finished");
        serverSocket.close();
    }
}