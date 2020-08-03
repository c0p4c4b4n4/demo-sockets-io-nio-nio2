package to_delete.nio2_v2.ASCDemo.server;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

public class Attachment {
    public AsynchronousServerSocketChannel serverSocketChannel;
    public AsynchronousSocketChannel socketChannel;
    public boolean isReadMode;
    public ByteBuffer buffer;
    public SocketAddress clientSocketAddress;
}