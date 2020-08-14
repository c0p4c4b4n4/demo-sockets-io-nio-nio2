package demo.nio2.completion_handler.server;

import demo.common.Demo;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class WriteCompletionHandler extends Demo implements CompletionHandler<Integer, Attachment> {

    private final AsynchronousSocketChannel socketChannel;

    WriteCompletionHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer bytesWritten, Attachment attachment) {
        logger.info("echo server wrote: {} byte(s)", bytesWritten);

        try {
            socketChannel.close();
            logger.info("connection closed");
        } catch (IOException e) {
            logger.error("exception during socket closing", e);
        }
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("exception during socket writing", t);
    }
}
