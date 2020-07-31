package nio_reactor;

import java.nio.channels.SelectionKey;

public interface EventHandler {

    void handleEvent(SelectionKey handle) throws Exception;
}
