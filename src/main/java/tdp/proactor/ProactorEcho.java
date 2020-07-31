package tdp.proactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ProactorInitiator {

    private static final int SERVER_PORT = 9001;

    public void initiateProactiveServer(int port) throws IOException {
        AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
        AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler(listener);

        SessionState state = new SessionState();
        listener.accept(state, acceptCompletionHandler);
    }

    public static void main(String[] args) {
        try {
            System.out.println("Async server listening on port : " + SERVER_PORT);
            new ProactorInitiator().initiateProactiveServer(SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sleep indefinitely since otherwise the JVM would terminate
        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, SessionState> {

    private final AsynchronousServerSocketChannel listener;

    public AcceptCompletionHandler(AsynchronousServerSocketChannel listener) {
        this.listener = listener;
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, SessionState sessionState) {
        // accept the next connection
        SessionState newSessionState = new SessionState();
        listener.accept(newSessionState, this);

        // handle this connection
        ByteBuffer inputBuffer = ByteBuffer.allocate(2048);
        ReadCompletionHandler readCompletionHandler = new ReadCompletionHandler(socketChannel, inputBuffer);
        socketChannel.read(inputBuffer, sessionState, readCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, SessionState sessionState) {
        // Handle connection failure...
    }
}

class ReadCompletionHandler implements CompletionHandler<Integer, SessionState> {

    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer inputBuffer;

    public ReadCompletionHandler(AsynchronousSocketChannel socketChannel, ByteBuffer inputBuffer) {
        this.socketChannel = socketChannel;
        this.inputBuffer = inputBuffer;
    }

    @Override
    public void completed(Integer bytesRead, SessionState sessionState) {

        byte[] buffer = new byte[bytesRead];
        inputBuffer.rewind();
        // Rewind the input buffer to read from the beginning

        inputBuffer.get(buffer);
        String message = new String(buffer);

        System.out.println("Received message from client : " +
                message);

        // Echo the message back to client
        WriteCompletionHandler writeCompletionHandler = new WriteCompletionHandler(socketChannel);

        ByteBuffer outputBuffer = ByteBuffer.wrap(buffer);

        socketChannel.write(outputBuffer, sessionState, writeCompletionHandler);
    }

    @Override
    public void failed(Throwable exc, SessionState attachment) {
        //Handle read failure.....
    }
}

class WriteCompletionHandler implements CompletionHandler<Integer, SessionState> {

    private final AsynchronousSocketChannel socketChannel;

    public WriteCompletionHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer bytesWritten, SessionState attachment) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, SessionState attachment) {
        // Handle write failure.....
    }
}


// used to hold client session specific state across a series of completion events
class SessionState {

    private Map<String, String> sessionProps = new ConcurrentHashMap<String, String>();

    public String getProperty(String key) {
        return sessionProps.get(key);
    }

    public void setProperty(String key, String value) {
        sessionProps.put(key, value);
    }
}