package demo.nio2.completion_handler.client;

import demo.common.Demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class ReadCompletionHandler extends Demo implements CompletionHandler<Integer, Attachment> {

    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer inputBuffer;

    ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer inputBuffer) {
        this.socketChannel = socketChannel;
        this.inputBuffer = inputBuffer;
    }

    @Override
    public void completed(Integer bytesRead, Attachment attachment) {
        logger.info("echo client read: {} byte(s)", bytesRead);
        try {
            inputBuffer.flip();
            logger.info("echo client received: " + StandardCharsets.UTF_8.newDecoder().decode(inputBuffer));

            attachment.getActive().set(false);
        } catch (IOException e) {
            logger.error("Exception during echo processing", e);
        }
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("exception during socket reading", t);
    }
}