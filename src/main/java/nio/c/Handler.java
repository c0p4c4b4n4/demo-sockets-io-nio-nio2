package nio.c;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

class Handler {

    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;

    private ByteBuffer readBuf = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuf = ByteBuffer.allocate(1024);

    public Handler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);

        // Register socketChannel with _selector listening on OP_READ events.
        // Callback: Handler, selected when the connection is established and ready for READ
        selectionKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(this);
        //selector.wakeup(); // let blocking select() return

        while (true) {
            if (selectionKey.isReadable()) {
                int numBytes = this.socketChannel.read(readBuf);
                System.out.println("read(): #bytes read into 'readBuf' buffer = " + numBytes);

                if (numBytes == -1) {
                    selectionKey.cancel();
                    this.socketChannel.close();
                    System.out.println("read(): client connection might have been dropped!");
                } else {

                    // Set the key's interest to WRITE operation
                    readBuf.flip();
                    byte[] bytes = new byte[readBuf.remaining()];
                    readBuf.get(bytes, 0, bytes.length);
                    System.out.print("process(): " + new String(bytes, Charset.forName("ISO-8859-1")));
                    writeBuf = ByteBuffer.wrap(bytes);
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                    selectionKey.selector().wakeup();
                }
            } else if (selectionKey.isWritable()) {
                int numBytes = 0;

                numBytes = this.socketChannel.write(writeBuf);
                System.out.println("write(): #bytes read from 'writeBuf' buffer = " + numBytes);

                if (numBytes > 0) {
                    readBuf.clear();
                    writeBuf.clear();

                    // Set the key's interest-set back to READ operation
                    selectionKey.interestOps(SelectionKey.OP_READ);
                    selectionKey.selector().wakeup();
                }
            }
        }
    }

}
