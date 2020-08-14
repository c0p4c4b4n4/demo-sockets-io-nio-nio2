package demo.nio2.completion_handler.client;

import demo.common.Demo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

class WriteCompletionHandler extends Demo implements CompletionHandler<Integer, Attachment> {

    private final AsynchronousSocketChannel socketChannel;

    WriteCompletionHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer bytesWritten, Attachment attachment) {
        logger.info("echo client wrote: {} byte(s)", bytesWritten);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, buffer);
        socketChannel.read(buffer, attachment, readCompletionHandler);
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("exception during socket writing", t);
    }
}