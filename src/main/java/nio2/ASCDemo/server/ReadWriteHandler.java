package nio2.ASCDemo.server;

import java.io.IOException;

import java.nio.channels.CompletionHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {

    @Override
    public void completed(Integer result, Attachment attachment) {
        try {
            if (result == -1) {
                attachment.channelClient.close();
                System.out.printf("Stopped listening to client %s%n", attachment.clientAddr);
            } else {
                if (attachment.isReadMode) {
                    attachment.buffer.flip();

                    int limit = attachment.buffer.limit();
                    byte[] bytes = new byte[limit];

                    attachment.buffer.get(bytes, 0, limit);
                    System.out.printf("Client at %s sends message: %s%n", attachment.clientAddr, new String(bytes, StandardCharsets.UTF_8));

                    attachment.isReadMode = false;

                    attachment.buffer.rewind();
                    attachment.channelClient.write(attachment.buffer, attachment, this);
                } else {
                    attachment.isReadMode = true;

                    attachment.buffer.clear();
                    attachment.channelClient.read(attachment.buffer, attachment, this);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to complete connection");
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable t, Attachment attachment) {
        System.out.println("Connection with client broken");
        t.printStackTrace();
    }
}