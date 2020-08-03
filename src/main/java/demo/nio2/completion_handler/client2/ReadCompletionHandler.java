package demo.nio2.completion_handler.client2;

import demo.common.Demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Arrays;

class ReadCompletionHandler extends Demo implements CompletionHandler<Integer, Attachment> {

    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer inputBuffer;

    ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer inputBuffer) {
        this.socketChannel = socketChannel;
        this.inputBuffer = inputBuffer;
    }

    private final static Charset CSUTF8 = Charset.forName("UTF-8");
    private BufferedReader conReader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void completed(Integer bytesRead, Attachment attachment) {
        inputBuffer.flip();
        int limit = inputBuffer.limit();
        byte[] bytes = new byte[limit];
        inputBuffer.get(bytes, 0, limit);
        String msg = new String(bytes, CSUTF8);
        logger.info("echo client received: " + msg);

        if (attachment.messages.length == 0) {
            attachment.active = false;
        } else {
            String message = attachment.messages[0];
            attachment.messages = Arrays.copyOfRange(attachment.messages, 1, attachment.messages.length);

            logger.info("echo client sent: " + message);

            ByteBuffer outputBuffer = ByteBuffer.wrap(message.getBytes()); // TODO charset in constructor
            WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);
            socketChannel.write(outputBuffer, attachment, writeCompletionHandler);
        }
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        logger.error("Exception during read", t);
    }
}