package nio2.ASCDemo.client;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;

public class Attachment {
    public AsynchronousSocketChannel socketChannel;
    public List<String> messages;
    public boolean isReadMode;
    public ByteBuffer buffer;
    public Thread mainThd;
}