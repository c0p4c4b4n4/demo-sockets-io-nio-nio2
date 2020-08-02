package patterns.reactor_echo;

import java.nio.channels.SelectionKey;

interface EventHandler {

    void handleEvent(SelectionKey handle) throws Exception;
}
