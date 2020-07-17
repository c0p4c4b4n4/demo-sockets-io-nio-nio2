package nio.time;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

/* Ask a list of hosts what time it is.  Demonstrates NIO socket channels
 * (connection and reading), buffer handling, charsets, and regular
 * expressions.
 */
public class TimeClient {

    private static final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

    private static final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(InetAddress.getByName("localhost"), 8013));

        // For simplicity we assume
        // that the time comes back to us in a single packet, so that we
        // only need to read once.
        buffer.clear();
        socketChannel.read(buffer);

        buffer.flip();
        CharBuffer cb = decoder.decode(buffer);

        System.out.print("client received: " + cb);
        socketChannel.close();
    }
}