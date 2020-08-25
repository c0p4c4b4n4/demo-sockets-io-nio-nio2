package demo.patterns.reactor.echo;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

class WriteEventHandler implements EventHandler {

    @Override
    public void handleEvent(SelectionKey handle) throws Exception {
        SocketChannel socketChannel = (SocketChannel) handle.channel();

        ByteBuffer buffer = (ByteBuffer) handle.attachment();
        socketChannel.write(buffer);
        socketChannel.close();
    }
}
