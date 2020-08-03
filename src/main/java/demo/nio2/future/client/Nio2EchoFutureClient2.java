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

public class Nio2EchoFutureClient2 {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final CharsetDecoder CHARSET_DECODER = CHARSET.newDecoder();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();

        socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
        socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024);
        socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        socketChannel.connect(new InetSocketAddress("localhost", 7000)).get();
        System.out.println("Local address: " + socketChannel.getLocalAddress());

        String[] messages = {"Alpha", "Bravo", "Charlie"};

        String message1 = messages[0];
        messages = Arrays.copyOfRange(messages, 1, messages.length);

        ByteBuffer outputBuffer = ByteBuffer.wrap(message1.getBytes());
        socketChannel.write(outputBuffer).get();

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(1024);
        while (socketChannel.read(inputBuffer).get() != -1) {
            inputBuffer.flip();
            System.out.println("echo client received: " + CHARSET_DECODER.decode(inputBuffer));

            if (inputBuffer.hasRemaining()) {
                inputBuffer.compact();
            } else {
                inputBuffer.clear();
            }

            if (messages.length == 0) {
                break;
            } else {
                String message2 = messages[0];
                messages = Arrays.copyOfRange(messages, 1, messages.length);

                ByteBuffer outputBuffer2 = ByteBuffer.wrap(message2.getBytes());
                socketChannel.write(outputBuffer2).get();
            }
        }
    }
}
