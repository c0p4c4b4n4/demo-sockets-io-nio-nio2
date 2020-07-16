package nio.c;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

class ReactiveEchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.socket().bind(new InetSocketAddress(9999));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();

            while (keysIterator.hasNext()) {
                SelectionKey key = keysIterator.next();
                keysIterator.remove();

                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    ByteBuffer readBuf = ByteBuffer.allocate(1024);
                    ByteBuffer writeBuf = ByteBuffer.allocate(1024);

                    // Register socketChannel with _selector listening on OP_READ events.
                    // Callback: Handler, selected when the connection is established and ready for READ
                    SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
//                    selectionKey.attach(this);
                    //selector.wakeup(); // let blocking select() return

                    while (true) {
                        if (selectionKey.isReadable()) {
                            int numBytes = socketChannel.read(readBuf);
                            System.out.println("read(): #bytes read into 'readBuf' buffer = " + numBytes);

                            if (numBytes == -1) {
                                selectionKey.cancel();
                                socketChannel.close();
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

                            numBytes = socketChannel.write(writeBuf);
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
        }
    }
}
