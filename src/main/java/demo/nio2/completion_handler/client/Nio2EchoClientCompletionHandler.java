package demo.nio2.completion_handler.client;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Nio2EchoClientCompletionHandler extends Demo {

    public static void main(String[] args) throws IOException {
        try (AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open()) {
            if (!socketChannel.isOpen()) {
                throw new IOException("Asynchronous  socket channel is not open !");
            }

            socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
            socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128 * 1024);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

            AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(socketChannel);
            socketChannel.connect(new InetSocketAddress("localhost", 7000), null, acceptCompletionHandler);

            System.in.read();
        }
    }

    private static class AcceptCompletionHandler implements CompletionHandler<Void, Void> {

        final ByteBuffer helloBuffer;
        final ByteBuffer buffer;
        private final AsynchronousSocketChannel socketChannel;
        CharBuffer charBuffer;
        ByteBuffer randomBuffer;
        final Charset charset;
        final CharsetDecoder decoder;

        public AcceptCompletionHandler(AsynchronousSocketChannel socketChannel) {
            this.socketChannel = socketChannel;
            helloBuffer = ByteBuffer.wrap("Hello !".getBytes());
            buffer = ByteBuffer.allocateDirect(1024);
            charBuffer = null;
            charset = Charset.defaultCharset();
            decoder = charset.newDecoder();
        }

        @Override
        public void completed(Void result, Void attachment) {
            try {
                logger.info("outgoing connection to: " + socketChannel.getRemoteAddress());

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
            } catch (IOException | InterruptedException | ExecutionException ex) {
                System.err.println(ex);
            } finally {
                try {
                    socketChannel.close();
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            throw new UnsupportedOperationException("Connection cannot be established!");
        }
    }
}
