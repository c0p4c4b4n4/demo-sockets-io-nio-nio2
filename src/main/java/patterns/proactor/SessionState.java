package patterns.proactor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
