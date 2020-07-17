package nio.time;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/* Ask a list of hosts what time it is.  Demonstrates NIO socket channels
 * (connection and reading), buffer handling, charsets, and regular
 * expressions.
 */
public class TimeClient {

    private static Charset charset = Charset.forName("US-ASCII");
    private static CharsetDecoder decoder = charset.newDecoder();

    private static ByteBuffer dbuf = ByteBuffer.allocateDirect(1024);

    public static void main(String[] args) throws IOException {
        SocketChannel sc = null;

        try {
            sc = SocketChannel.open();
            sc.connect(new InetSocketAddress(InetAddress.getByName("localhost"), 8013));

            // Read the time from the remote host.  For simplicity we assume
            // that the time comes back to us in a single packet, so that we
            // only need to read once.
            dbuf.clear();
            sc.read(dbuf);

            // Print the remote address and the received time
            dbuf.flip();
            CharBuffer cb = decoder.decode(dbuf);
            System.out.print(" : " + cb);
        } finally {
            if (sc != null)
                sc.close();
        }
    }
}