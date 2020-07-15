package nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;

public class SelectorClient {
    private static final int DEFAULT_PORT = 9999;

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", port));

        ByteBuffer bb = ByteBuffer.allocateDirect(8);

        long time = 0;
        while (socketChannel.read(bb) != -1) {
            bb.flip();
            while (bb.hasRemaining()) {
                time <<= 8;
                time |= bb.get() & 255;
            }
            bb.clear();
        }
        System.out.println(new Date(time));

        socketChannel.close();
    }
}