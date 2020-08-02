package nio2.b;

import nio2.ASCDemo.server.Attachment;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Map;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {

    @Override
    public void completed(Integer result, Map<String, Object> attachment) {
        Map<String, Object> actionInfo = attachment;
        String action = (String) actionInfo.get("action");
        if ("read".equals(action)) {
            ByteBuffer buffer = (ByteBuffer) actionInfo.get("buffer");
            buffer.flip();
            actionInfo.put("action", "write");
            clientChannel.write(buffer, actionInfo, this);
            buffer.clear();
        } else if ("write".equals(action)) {
            ByteBuffer buffer = ByteBuffer.allocate(32);
            actionInfo.put("action", "read");
            actionInfo.put("buffer", buffer);
            clientChannel.read(buffer, actionInfo, this);
        }

    }

    @Override
    public void failed(Throwable exc, Map<String, Object> attachment) {

    }

}
}
