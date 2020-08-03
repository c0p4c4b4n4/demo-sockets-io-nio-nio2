package demo.nio2.completion_handler.client2;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class Attachment {

    public AsynchronousSocketChannel socketChannel;
    public String[] messages;
    public volatile boolean active;


    public boolean isReadMode;
    public ByteBuffer buffer;
    public Thread mainThd;
}