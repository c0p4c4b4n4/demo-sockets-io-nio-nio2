package nio.selector_time;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;

public class TimeBinClient {
    private static final int DEFAULT_PORT = 9999;

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", port));

        long time = 0;

        ByteBuffer buffer = ByteBuffer.allocateDirect(8);
        while (socketChannel.read(buffer) != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                time <<= 8;
                time |= buffer.get() & 255;
            }
            buffer.clear();
        }

        System.out.println(new Date(time));

        socketChannel.close();
    }
}