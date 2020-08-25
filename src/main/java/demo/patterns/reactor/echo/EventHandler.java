package demo.patterns.reactor.echo;

import java.io.IOException;
import java.nio.channels.SelectionKey;

interface EventHandler {

    void handleEvent(SelectionKey handle) throws IOException;
}
