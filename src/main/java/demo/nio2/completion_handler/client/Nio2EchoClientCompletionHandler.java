package demo.nio2.completion_handler.client;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Nio2EchoClientCompletionHandler extends Demo {

    private static final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

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
            logger.info("echo client finished");
        }
    }

    private static class AcceptCompletionHandler implements CompletionHandler<Void, Void> {

        final ByteBuffer outputBuffer;
        final ByteBuffer inputBuffer;
        private final AsynchronousSocketChannel socketChannel;
        ByteBuffer randomBuffer;

        public AcceptCompletionHandler(AsynchronousSocketChannel socketChannel) {
            this.socketChannel = socketChannel;
            outputBuffer = ByteBuffer.wrap("Hello !".getBytes());
            inputBuffer = ByteBuffer.allocateDirect(1024);
        }

        @Override
        public void completed(Void result, Void attachment) {
            try {
                logger.info("outgoing connection to: " + socketChannel.getRemoteAddress());

                socketChannel.write(outputBuffer).get();

                outputBuffer.flip();
                logger.info("echo client sent: " + decoder.decode(outputBuffer).toString());

                while (socketChannel.read(inputBuffer).get() != -1) {
                    inputBuffer.flip();

                    logger.info("echo client received: " + decoder.decode(inputBuffer).toString());

                    if (inputBuffer.hasRemaining()) {
                        inputBuffer.compact();
                    } else {
                        inputBuffer.clear();
                    }

                    int r = new Random().nextInt(10);
                    if (r == 0) {
                        System.out.println("Close the asynchronous socket channel!");
                        break;
                    } else {
                        randomBuffer = ByteBuffer.wrap("Random number:".concat(String.valueOf(r)).getBytes());
                        socketChannel.write(randomBuffer).get();

                        outputBuffer.flip();
                        logger.info("echo client sent: " + decoder.decode(randomBuffer).toString());
                    }
                }
            } catch (IOException | InterruptedException | ExecutionException e) {
                logger.error("Exception during echo processing", e);
            } finally {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    logger.error("Exception during asynchronous socket channel close", e);
                }
            }
        }

        @Override
        public void failed(Throwable e, Void attachment) {
            logger.error("Connection cannot be established",e);
        }
    }
}
