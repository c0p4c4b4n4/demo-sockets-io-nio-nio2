package demo.nio2.completion_handler.server0;

import demo.common.Demo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class ReadCompletionHandler extends Demo implements CompletionHandler<Integer, Attachment> {

    private final AsynchronousServerSocketChannel serverSocketChannel;
    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer inputBuffer;

    public ReadCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel, AsynchronousSocketChannel socketChannel, ByteBuffer inputBuffer) {
        this.serverSocketChannel = serverSocketChannel;
        this.socketChannel = socketChannel;
        this.inputBuffer = inputBuffer;
    }

    @Override
    public void completed(Integer bytesRead, Attachment attachment) {
        logger.info("read: " + bytesRead);

        byte[] buffer = new byte[bytesRead];
        inputBuffer.rewind();

        inputBuffer.get(buffer);
        String message = new String(buffer);
        logger.info("Received message from client : " + message);

        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(serverSocketChannel, socketChannel);
        ByteBuffer outputBuffer = ByteBuffer.wrap(buffer);
        socketChannel.write(outputBuffer, attachment, writeCompletionHandler);
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("Exception during read", t);
    }
}
