package nio.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class TimeBlockingServer {

    private static final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        System.out.println("is blocking: " + serverSocketChannel.isBlocking());
        serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 9002));

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();

            String msg = LocalDateTime.now().toString() + "\r\n";
            socketChannel.write(encoder.encode(CharBuffer.wrap(msg)));
            System.out.println("server sent: " + msg);

            socketChannel.close();
        }
    }
}