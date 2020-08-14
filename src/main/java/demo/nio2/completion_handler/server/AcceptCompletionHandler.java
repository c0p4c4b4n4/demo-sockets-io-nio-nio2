package demo.nio2.completion_handler.server;

import demo.common.Demo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class AcceptCompletionHandler extends Demo implements CompletionHandler<AsynchronousSocketChannel, Void> {

    private final AsynchronousServerSocketChannel serverSocketChannel;

    AcceptCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
        logger.info("connection accepted: {}", socketChannel);

        serverSocketChannel.accept(null, this);

        ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, inputBuffer);
        socketChannel.read(inputBuffer, null, readCompletionHandler);
    }

    @Override
    public void failed(Throwable t, Void attachment) {
        logger.error("exception during socket accepting", t);
    }
}
