package nio.selector_echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EchoClient {

    public static void main(String[] args) throws IOException {
        SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 9999));

        String msg = "hello";

        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());

        client.write(buffer);
        buffer.clear();
        client.read(buffer);
        String response = new String(buffer.array()).trim();
        System.out.println("response=" + response);
        buffer.clear();

        client.close();
    }
}
