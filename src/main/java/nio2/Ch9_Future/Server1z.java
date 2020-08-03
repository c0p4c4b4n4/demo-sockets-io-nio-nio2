package nio2.Ch9_Future;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Server1z {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 7000));

        Future<AsynchronousSocketChannel> acceptResult = serverSocketChannel.accept();
        AsynchronousSocketChannel clientChannel = acceptResult.get();

        if ((clientChannel != null) && (clientChannel.isOpen())) {
            while (true) {
                ByteBuffer buffer = ByteBuffer.allocate(32);
                Future<Integer> readResult = clientChannel.read(buffer);

                readResult.get();

                buffer.flip();
                String message = new String(buffer.array()).trim();
                if (message.equals("bye")) {
                    break;
                }
                buffer = ByteBuffer.wrap(new String(message).getBytes());
                Future<Integer> writeResult = clientChannel.write(buffer);

                // do some computation
                writeResult.get();
                buffer.clear();
            }

            clientChannel.close();
            serverSocketChannel.close();
        }
    }
}