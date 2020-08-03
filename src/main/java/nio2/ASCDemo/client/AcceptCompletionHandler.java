package nio2.ASCDemo.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class AcceptCompletionHandler implements CompletionHandler<Void, Attachment> {

    private final AsynchronousSocketChannel socketChannel;

     AcceptCompletionHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Void result, Attachment attachment) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, inputBuffer);
        socketChannel.read(inputBuffer, attachment, readCompletionHandler);
    }

    @Override
    public void failed(Throwable e, Attachment attachment) {
    }
}
