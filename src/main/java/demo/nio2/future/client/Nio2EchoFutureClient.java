package demo.nio2.future.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Nio2EchoFutureClient {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final CharsetDecoder CHARSET_DECODER = CHARSET.newDecoder();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        String[] messages = {"Alpha", "Bravo", "Charlie"};
        for (String message : messages) {
            AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();

            socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
            socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

            socketChannel.connect(new InetSocketAddress("localhost", 7000)).get();

            ByteBuffer outputBuffer = ByteBuffer.wrap(message.getBytes());
            socketChannel.write(outputBuffer).get();
            System.out.println("echo client sent: " + message);

            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(1024);
            while (socketChannel.read(inputBuffer).get() != -1) {
                inputBuffer.flip();
                System.out.println("echo client received: " + CHARSET_DECODER.decode(inputBuffer));

                if (inputBuffer.hasRemaining()) {
                    inputBuffer.compact();
                } else {
                    inputBuffer.clear();
                }
            }

            socketChannel.close();
        }
    }
}
