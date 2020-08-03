package demo.nio2.completion_handler.server0;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class ReadCompletionHandler implements CompletionHandler<Integer, Attachment> {

    private final AsynchronousServerSocketChannel serverSocketChannel;
    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer inputBuffer;

    public ReadCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel, AsynchronousSocketChannel socketChannel, ByteBuffer inputBuffer) {
        this.serverSocketChannel = serverSocketChannel;
        this.socketChannel = socketChannel;
        this.inputBuffer = inputBuffer;
    }

    @Override
    public void completed(Integer bytesRead, Attachment attachment) {
        byte[] buffer = new byte[bytesRead];
        inputBuffer.rewind();

        inputBuffer.get(buffer);
        String message = new String(buffer);
        System.out.println("Received message from client : " + message);

        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(serverSocketChannel, socketChannel);
        ByteBuffer outputBuffer = ByteBuffer.wrap(buffer);
        socketChannel.write(outputBuffer, attachment, writeCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        //Handle read failure.....
    }
}
