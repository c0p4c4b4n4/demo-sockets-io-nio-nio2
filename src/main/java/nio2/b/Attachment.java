package nio2.b;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

public class Attachment {

    AsynchronousServerSocketChannel serverChannel;
    AsynchronousSocketChannel clientChannel;
    Action action;
    ByteBuffer buffer;

    enum Action {read, write}
}
