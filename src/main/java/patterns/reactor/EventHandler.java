package patterns.reactor;

import java.nio.channels.SelectionKey;

interface EventHandler {

    void handleEvent(SelectionKey handle) throws Exception;
}
