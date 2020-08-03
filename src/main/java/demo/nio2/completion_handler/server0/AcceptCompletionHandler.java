package demo.nio2.completion_handler.server0;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    private final AsynchronousServerSocketChannel serverSocketChannel;

    AcceptCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Attachment attachment) {
        Attachment newAttachment = new Attachment();
        serverSocketChannel.accept(newAttachment, this);

        ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(serverSocketChannel, socketChannel, inputBuffer);
        socketChannel.read(inputBuffer, attachment, readCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
    }
}
