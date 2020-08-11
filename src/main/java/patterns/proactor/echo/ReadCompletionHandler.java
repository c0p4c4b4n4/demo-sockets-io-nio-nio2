package patterns.proactor.echo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class ReadCompletionHandler implements CompletionHandler<Integer, SessionState> {

    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer inputBuffer;

    ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer inputBuffer) {
        this.socketChannel = socketChannel;
        this.inputBuffer = inputBuffer;
    }

    @Override
    public void completed(Integer bytesRead, SessionState sessionState) {
        byte[] bytes = new byte[bytesRead];
        inputBuffer.rewind();
        // Rewind the input buffer to read from the beginning

        inputBuffer.get(bytes);
        String message = new String(bytes);

        System.out.println("Received message from client : " + message);

        // Echo the message back to client
        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);

        ByteBuffer outputBuffer = ByteBuffer.wrap(bytes);

        socketChannel.write(outputBuffer, sessionState, writeCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, SessionState attachment) {
        //Handle read failure.....
    }
}
