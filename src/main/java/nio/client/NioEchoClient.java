package nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class NioEchoClient {

    private static final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] msgs = {"Alpha", "Bravo", "Charlie"};
        for (String msg : msgs) {
            System.out.println("echo client started");
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 7000));

            ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
            socketChannel.write(buffer);
            System.out.println("echo client sent: " + msg);

            int totalRead = 0;
            while (totalRead < msg.getBytes().length) {
                buffer.clear();

                int read = socketChannel.read(buffer);
                if (read <= 0)
                    break;

                totalRead += read;

                buffer.flip();
                System.out.println("echo client received: " + decoder.decode(buffer));
            }

            socketChannel.close();
            System.out.println("echo client disconnected");
            Thread.sleep(1000);
        }
    }
}
