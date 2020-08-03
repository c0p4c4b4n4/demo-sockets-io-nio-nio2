package demo.nio2.completion_handler.client2;

import demo.common.Demo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Arrays;

class AcceptCompletionHandler extends Demo implements CompletionHandler<Void, Attachment> {

    private final AsynchronousSocketChannel socketChannel;
    private final Charset charset;

    AcceptCompletionHandler(AsynchronousSocketChannel socketChannel, Charset CHARSET) {
        this.socketChannel = socketChannel;
        this.charset = CHARSET;
    }

    @Override
    public void completed(Void result, Attachment attachment) {
        String message = attachment.messages[0];
        attachment.messages = Arrays.copyOfRange(attachment.messages, 1, attachment.messages.length);

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
