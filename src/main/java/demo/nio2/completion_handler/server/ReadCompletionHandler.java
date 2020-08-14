package demo.nio2.completion_handler.server;

import demo.common.Demo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
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
        logger.info("echo server read: {} byte(s)", bytesRead);

        byte[] bytes = new byte[bytesRead];
        inputBuffer.rewind();

        inputBuffer.get(bytes);
        String message = new String(bytes, StandardCharsets.UTF_8);
        logger.info("echo server received: {}", message);

        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
        ByteBuffer outputBuffer = ByteBuffer.wrap(bytes);
        socketChannel.write(outputBuffer, attachment, writeCompletionHandler);
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("Exception during read", t);
    }
}
