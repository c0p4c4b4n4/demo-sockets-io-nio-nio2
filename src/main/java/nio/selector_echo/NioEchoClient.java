package nio.selector_echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class NioEchoClient {

    private static final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

    public static void main(String[] args) throws IOException {
        System.out.println("echo client started");
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9001));

        String msg = "abcdefghijklmnopqrstuvwxyz";
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(buffer);
        System.out.println("echo client sent: " + msg);

        while (true) {
            buffer.clear();

            int n = socketChannel.read(buffer);
            if (n <= 0)
                break;

            buffer.flip();
            System.out.println("echo client received: " + decoder.decode(buffer));
        }

        socketChannel.close();
        System.out.println("echo client finished");
    }
}
