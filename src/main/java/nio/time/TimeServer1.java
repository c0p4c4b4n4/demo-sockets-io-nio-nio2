package nio.time;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class TimeServer1 {

    private static final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress("localhost", 8013));

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();

            String msg = LocalDateTime.now().toString() + "\r\n";
            socketChannel.write(encoder.encode(CharBuffer.wrap(msg)));
            System.out.println("server sent: " + msg);

            socketChannel.close();
        }
    }
}