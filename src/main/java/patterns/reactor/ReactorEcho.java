package patterns.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class ReactorEcho {

    private static final int SERVER_PORT = 9001;

    public static void main(String[] args) throws IOException {
        System.out.println("Starting NIO server at port: " + SERVER_PORT);
        new ReactorInitiator().initiateReactiveServer(SERVER_PORT);
    }
}
