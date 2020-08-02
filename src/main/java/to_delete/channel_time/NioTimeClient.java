package to_delete.channel_time;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class NioTimeClient {

    private static final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

    public static void main(String[] args) throws IOException {
        System.out.println("time client started");

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        socketChannel.connect(new InetSocketAddress("localhost", 9002));

        while (!socketChannel.finishConnect())
            System.out.println("waiting to finish connection");

        ByteBuffer buffer = ByteBuffer.allocate(10);
        while (socketChannel.read(buffer) >= 0) {
            buffer.flip();

            while (buffer.hasRemaining())
                System.out.print((char) buffer.get());

            buffer.clear();
        }

        socketChannel.close();
        System.out.println("time client finished");
    }
}