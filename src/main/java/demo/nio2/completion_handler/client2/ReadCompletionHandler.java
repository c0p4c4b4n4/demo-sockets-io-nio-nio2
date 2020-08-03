package demo.nio2.completion_handler.client2;

import demo.common.Demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Arrays;

class ReadCompletionHandler extends Demo implements CompletionHandler<Integer, Attachment> {

    private final AsynchronousSocketChannel socketChannel;
    private final Charset charset;
    private final ByteBuffer inputBuffer;

    ReadCompletionHandler(AsynchronousSocketChannel socketChannel, Charset charset, ByteBuffer inputBuffer) {
        this.socketChannel = socketChannel;
        this.charset = charset;
        this.inputBuffer = inputBuffer;
    }

    @Override
    public void completed(Integer bytesRead, Attachment attachment) {
        try {
            inputBuffer.flip();
            logger.info("echo client received: " + charset.newDecoder().decode(inputBuffer));

            if (attachment.messages.length == 0) {
                attachment.active = false;
            } else {
                socketChannel.close();

                String message = attachment.messages[0];
                attachment.messages = Arrays.copyOfRange(attachment.messages, 1, attachment.messages.length);

//                logger.info("echo client sent: " + message);
//
//                ByteBuffer outputBuffer = ByteBuffer.wrap(message.getBytes(charset));
//                WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel, charset);
//                socketChannel.write(outputBuffer, attachment, writeCompletionHandler);

                AsynchronousSocketChannel socketChannel2 = AsynchronousSocketChannel.open();
                AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(socketChannel2, charset);
                socketChannel2.connect(new InetSocketAddress("localhost", 7000), attachment, acceptCompletionHandler);
            }
        } catch (IOException e) {
            logger.error("Exception during echo processing", e);
        }
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("Exception during read", t);
    }
}