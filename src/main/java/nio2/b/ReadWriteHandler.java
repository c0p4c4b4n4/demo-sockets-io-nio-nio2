package nio2.b;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Map;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {

    @Override
    public void completed(Integer result, Attachment attachment) {
        if (attachment.action == Attachment.Action.read) {
            ByteBuffer buffer = (ByteBuffer) attachment.buffer;
            buffer.flip();

            attachment.action = Attachment.Action.write;
            attachment.clientChannel.write(buffer, attachment, this);
            buffer.clear();
        } else if (attachment.action == Attachment.Action.write) {
            ByteBuffer buffer = ByteBuffer.allocate(32);
            attachment.action = Attachment.Action.read;
            attachment.buffer = buffer;
            attachment.clientChannel.read(buffer, attachment, this);
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
    }
}
