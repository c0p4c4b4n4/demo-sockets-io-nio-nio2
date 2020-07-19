package nio.selector_echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioEchoClient {

    public static void main(String[] args) throws IOException {
        System.out.println("echo client started");
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 9001));

        String msg = "abcdefghijklmnopqrstuvwxyz";
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(buffer);
        System.out.println("echo client sent: " + msg);

        buffer.clear();
        socketChannel.read(buffer);
        System.out.println("echo client received: " + new String(buffer.array()));
        buffer.clear();

        socketChannel.close();
        System.out.println("echo client finished");
    }
}
