package nio.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChannelClient {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        InetSocketAddress remoteAddress = new InetSocketAddress("localhost", 9999);
        socketChannel.connect(remoteAddress);

        while (!socketChannel.finishConnect())
            System.out.println("waiting to finish connection");

        ByteBuffer buffer = ByteBuffer.allocate(200);
        while (socketChannel.read(buffer) >= 0) {
            buffer.flip();

            while (buffer.hasRemaining())
                System.out.print((char) buffer.get());

            buffer.clear();
        }
        socketChannel.close();
    }
}