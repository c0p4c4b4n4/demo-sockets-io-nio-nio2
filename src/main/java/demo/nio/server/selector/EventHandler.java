package demo.nio.server.selector;

import java.nio.channels.SelectionKey;

interface EventHandler {

    void handleEvent(SelectionKey handle) throws Exception;
}
