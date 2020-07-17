package oracle.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Date;

/* Listen for connections and tell callers what time it is.
 * Demonstrates NIO socket channels (accepting and writing),
 * buffer handling, charsets, and regular expressions.
 */
public class TimeServer {

    private static Charset charset = Charset.forName("US-ASCII");
    private static CharsetEncoder encoder = charset.newEncoder();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("localhost", 8013));

        while (true) {
            SocketChannel sc = ssc.accept();
            try {
                String now = new Date().toString();
                sc.write(encoder.encode(CharBuffer.wrap(now + "\r\n")));
                System.out.println(sc.socket().getInetAddress() + " : " + now);
                sc.close();
            } finally {
                sc.close();
            }
        }
    }
}