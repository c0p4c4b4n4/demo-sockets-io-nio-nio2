package nio2.b;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    @Override
    public void completed(AsynchronousSocketChannel clientChannel, Attachment attachment) {
        if (attachment.serverChannel.isOpen())
            attachment.serverChannel.accept(null, this);

        attachment.clientChannel = clientChannel;

        if ((attachment.clientChannel != null) && (attachment.clientChannel.isOpen())) {
            ReadWriteHandler handler = new ReadWriteHandler();
            ByteBuffer buffer = ByteBuffer.allocate(32);

            Attachment attachment2 = new Attachment();
            attachment2.serverChannel = attachment.serverChannel;
            attachment2.clientChannel = clientChannel;
            attachment2.action = Attachment.Action.read;
            attachment2.buffer = buffer;
            attachment.clientChannel.read(buffer, attachment2, handler);
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        // process error
    }
}
