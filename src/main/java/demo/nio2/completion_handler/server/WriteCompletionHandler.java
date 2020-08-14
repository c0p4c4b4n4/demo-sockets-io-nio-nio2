package demo.nio2.completion_handler.server;

import demo.common.Demo;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class WriteCompletionHandler extends Demo implements CompletionHandler<Integer, Attachment> {

    private final AsynchronousServerSocketChannel serverSocketChannel;
    private final AsynchronousSocketChannel socketChannel;

    WriteCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel, AsynchronousSocketChannel socketChannel) {
        this.serverSocketChannel = serverSocketChannel;
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer bytesWritten, Attachment attachment) {
        logger.info("echo server wrote: {} byte(s)", bytesWritten);

        try {
            socketChannel.close();
            logger.info("connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("Exception during write", t);
    }
}
