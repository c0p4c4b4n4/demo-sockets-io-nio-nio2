package demo.nio.server.selector;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class WriteEventHandler implements EventHandler {

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("write");

        SocketChannel socketChannel = (SocketChannel) handle.channel();
        ByteBuffer inputBuffer = (ByteBuffer) handle.attachment();
        socketChannel.write(inputBuffer);
        socketChannel.close();
    }
}
