package patterns.reactor_echo;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

class ReadEventHandler implements EventHandler {

    private final Selector demultiplexer;
    private final ByteBuffer inputBuffer = ByteBuffer.allocate(2048);

    public ReadEventHandler(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("read");

        SocketChannel socketChannel = (SocketChannel) handle.channel();

        socketChannel.read(inputBuffer); // Read data from client

        inputBuffer.flip();
        // Rewind the buffer to start reading from the beginning

        byte[] buffer = new byte[inputBuffer.limit()];
        inputBuffer.get(buffer);

        System.out.println("Received message from client : " + new String(buffer));
        inputBuffer.flip();
        // Rewind the buffer to start reading from the beginning
        // Register the interest for writable readiness event for
        // this channel in order to echo back the message

        socketChannel.register(demultiplexer, SelectionKey.OP_WRITE, inputBuffer);
    }
}
