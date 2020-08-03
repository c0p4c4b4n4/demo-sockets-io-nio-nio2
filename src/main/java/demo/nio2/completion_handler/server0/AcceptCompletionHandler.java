package demo.nio2.completion_handler.server0;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    private final AsynchronousServerSocketChannel listener;

    public AcceptCompletionHandler(AsynchronousServerSocketChannel listener) {
        this.listener = listener;
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Attachment attachment) {
        // accept the next connection
        Attachment newAttachment = new Attachment();
        listener.accept(newAttachment, this);

        // handle this connection
        ByteBuffer inputBuffer = ByteBuffer.allocate(2048);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, inputBuffer);
        socketChannel.read(inputBuffer, attachment, readCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        // Handle connection failure...
    }
}
