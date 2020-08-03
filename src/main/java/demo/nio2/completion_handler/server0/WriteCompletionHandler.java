package demo.nio2.completion_handler.server0;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class WriteCompletionHandler implements CompletionHandler<Integer, Attachment> {

    private final AsynchronousServerSocketChannel serverSocketChannel;
    private final AsynchronousSocketChannel socketChannel;

    public WriteCompletionHandler(AsynchronousServerSocketChannel serverSocketChannel, AsynchronousSocketChannel socketChannel) {
        this.serverSocketChannel = serverSocketChannel;
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer bytesWritten, Attachment attachment) {

//        try {
//            socketChannel.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        // Handle write failure.....
    }
}
