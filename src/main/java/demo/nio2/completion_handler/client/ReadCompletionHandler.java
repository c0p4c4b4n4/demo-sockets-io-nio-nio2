package demo.nio2.completion_handler.client;

import demo.common.Demo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

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
        logger.info("read: " + bytesRead);
        try {
            inputBuffer.flip();
            logger.info("echo client received: " + charset.newDecoder().decode(inputBuffer));

            attachment.active = false;
        } catch (IOException e) {
            logger.error("Exception during echo processing", e);
        }
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("Exception during read", t);
    }
}