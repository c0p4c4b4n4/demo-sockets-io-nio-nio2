package demo.nio2.future.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Nio2EchoFutureClient2 {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        ByteBuffer helloBuffer = ByteBuffer.wrap("Hello !".getBytes());
        ByteBuffer randomBuffer;
        CharBuffer charBuffer;
        Charset charset = Charset.defaultCharset();
        CharsetDecoder decoder = charset.newDecoder();

        try (AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open()) {
            if (!socketChannel.isOpen()) {
                throw new IOException("Asynchronous  socket channel is not open !");
            }

            socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
            socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

            socketChannel.connect(new InetSocketAddress("localhost", 7000)).get();
            System.out.println("Local address: " + socketChannel.getLocalAddress());

            socketChannel.write(helloBuffer).get();

            while (socketChannel.read(buffer).get() != -1) {
                buffer.flip();

                charBuffer = decoder.decode(buffer);
                System.out.println(charBuffer.toString());

                if (buffer.hasRemaining()) {
                    buffer.compact();
                } else {
                    buffer.clear();
                }

                int r = new Random().nextInt(100);
                if (r == 50) {
                    System.out.println("50 was generated! Close the asynchronous socket channel!");
                    break;
                } else {
                    randomBuffer = ByteBuffer.wrap("Random number:".concat(String.valueOf(r)).getBytes());
                    socketChannel.write(randomBuffer).get();
                }
            }
        }
    }
}
