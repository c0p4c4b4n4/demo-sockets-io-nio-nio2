package nio.server.channel_echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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

            String msg = LocalDateTime.now().toString() + "\r\n";
            socketChannel.write(encoder.encode(CharBuffer.wrap(msg)));
            System.out.println("time server sent: " + msg);

            socketChannel.close();
        }

        System.out.println("time server finished");
        serverSocket.close();
    }
}