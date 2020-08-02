package nio2.b;

import nio2.ASCDemo.server.Attachment;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    @Override
    public void completed(AsynchronousSocketChannel result, Attachment attachment) {
        if (serverChannel.isOpen())
            serverChannel.accept(null, this);
        clientChannel = result;
        if ((clientChannel != null) && (clientChannel.isOpen())) {
            ReadWriteHandler handler = new ReadWriteHandler();
            ByteBuffer buffer = ByteBuffer.allocate(32);
            Map<String, Object> readInfo = new HashMap<>();
            readInfo.put("action", "read");
            readInfo.put("buffer", buffer);
            clientChannel.read(buffer, readInfo, handler);
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        // process error
    }
}
