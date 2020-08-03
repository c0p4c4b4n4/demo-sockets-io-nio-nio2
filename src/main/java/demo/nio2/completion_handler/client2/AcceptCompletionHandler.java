package demo.nio2.completion_handler.client2;

import demo.common.Demo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Arrays;

class AcceptCompletionHandler extends Demo implements CompletionHandler<Void, Attachment> {

    private final AsynchronousSocketChannel socketChannel;

    AcceptCompletionHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Void result, Attachment attachment) {
        String message = attachment.messages[0];
        attachment.messages = Arrays.copyOfRange(attachment.messages, 1, attachment.messages.length);

        logger.info("echo client sent: " + message);

        ByteBuffer outputBuffer = ByteBuffer.wrap(message.getBytes()); // TODO charset in constructor
        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
        socketChannel.write(outputBuffer, attachment, writeCompletionHandler);
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("Exception during accept connection", t);
    }
}
