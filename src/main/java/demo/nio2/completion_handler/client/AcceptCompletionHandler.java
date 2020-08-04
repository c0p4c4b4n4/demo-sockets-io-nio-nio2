package demo.nio2.completion_handler.client;

import demo.common.Demo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

class AcceptCompletionHandler extends Demo implements CompletionHandler<Void, Attachment> {

    private final AsynchronousSocketChannel socketChannel;
    private final Charset charset;

    AcceptCompletionHandler(AsynchronousSocketChannel socketChannel, Charset CHARSET) {
        this.socketChannel = socketChannel;
        this.charset = CHARSET;
    }

    @Override
    public void completed(Void result, Attachment attachment) {
        logger.info("accepted");

        String message = attachment.message;
        logger.info("echo client sent: " + message);

        ByteBuffer outputBuffer = ByteBuffer.wrap(message.getBytes(charset));
        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel, charset);
        socketChannel.write(outputBuffer, attachment, writeCompletionHandler);
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("Exception during accept connection", t);
    }
}
