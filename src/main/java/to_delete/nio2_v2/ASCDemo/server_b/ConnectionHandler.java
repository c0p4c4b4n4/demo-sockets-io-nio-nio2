package to_delete.nio2_v2.ASCDemo.server_b;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {

    @Override
    public void completed(AsynchronousSocketChannel clientChannel, Attachment attachment) {
//        if (attachment.serverChannel.isOpen())
        try {
            attachment.serverChannel.accept(attachment, this);
            System.out.printf("Accepted connection from %s%n", clientChannel.getRemoteAddress());

            attachment.clientChannel = clientChannel;

//            if ((attachment.clientChannel != null) && (attachment.clientChannel.isOpen())) {
                ByteBuffer buffer = ByteBuffer.allocate(32);

                Attachment attachment2 = new Attachment();
                attachment2.serverChannel = attachment.serverChannel;
                attachment2.clientChannel = clientChannel;
                attachment2.action = Attachment.Action.read;
                attachment2.buffer = buffer;

                ReadWriteHandler handler = new ReadWriteHandler();
                attachment.clientChannel.read(buffer, attachment2, handler);
//            }
        } catch (IOException e) {
            System.out.println("Failed to complete connection");
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        // process error
    }
}
