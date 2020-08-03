package demo.nio2.completion_handler.client1;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class Nio2EchoClientCompletionHandler1 extends Demo {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final CharsetDecoder CHARSET_DECODER = CHARSET.newDecoder();

    private static volatile boolean active = true;

    public static void main(String[] args) throws IOException {
        try (AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open()) {
            if (!socketChannel.isOpen()) {
                throw new IOException("Asynchronous  socket channel is not open !");
            }

            socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024);
            socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024);
            socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

            String[] messages = new String[] {"Alpha", "Bravo", "Charlie"};
            AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(socketChannel, messages);
            socketChannel.connect(new InetSocketAddress("localhost", 7000), null, acceptCompletionHandler);

            while (active) {
            }

            logger.info("echo client finished");
        }
    }

    private static class AcceptCompletionHandler implements CompletionHandler<Void, Void> {

        private final AsynchronousSocketChannel socketChannel;
        private String[] messages;
        private ByteBuffer outputBuffer;
        private ByteBuffer inputBuffer;

        AcceptCompletionHandler(AsynchronousSocketChannel socketChannel, String[] messages) {
            this.socketChannel = socketChannel;
            this.messages = messages;
            this.outputBuffer = null;
            this.inputBuffer = ByteBuffer.allocateDirect(1024);
        }

        @Override
        public void completed(Void result, Void attachment) {
            try {
                logger.info("outgoing connection to: " + socketChannel.getRemoteAddress());

                String message1 = messages[0];
                messages = Arrays.copyOfRange(messages, 1, messages.length);

                outputBuffer = ByteBuffer.wrap(message1.getBytes(CHARSET));
                socketChannel.write(outputBuffer).get(); // blocked

                outputBuffer.flip();
                logger.info("echo client sent: " + CHARSET_DECODER.decode(outputBuffer).toString());

                while (socketChannel.read(inputBuffer).get() != -1) { // blocked
                    inputBuffer.flip();
                    logger.info("echo client received: " + CHARSET_DECODER.decode(inputBuffer).toString());

                    if (inputBuffer.hasRemaining()) {
                        inputBuffer.compact();
                    } else {
                        inputBuffer.clear();
                    }

                    if (messages.length == 0) {
                        active = false;
                        break;
                    } else {
                        String message2 = messages[0];
                        messages = Arrays.copyOfRange(messages, 1, messages.length);

                        outputBuffer = ByteBuffer.wrap(message2.getBytes(CHARSET));
                        socketChannel.write(outputBuffer).get(); // blocked

                        outputBuffer.flip();
                        logger.info("echo client sent: " + CHARSET_DECODER.decode(outputBuffer).toString());
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
            logger.error("Connection cannot be established", e);
        }
    }
}
