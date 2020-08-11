package demo.patterns.reactor.echo;

import java.nio.channels.SelectionKey;

interface EventHandler {

    void handleEvent(SelectionKey handle) throws Exception;
}
