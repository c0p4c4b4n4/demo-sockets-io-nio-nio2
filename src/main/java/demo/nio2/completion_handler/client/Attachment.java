package demo.nio2.completion_handler.client;

import java.util.concurrent.atomic.AtomicBoolean;

public class Attachment {

    private final String message;
    private final AtomicBoolean active;

    public Attachment(String message, boolean active) {
        this.message = message;
        this.active = new AtomicBoolean(active);
    }

    public String getMessage() {
        return message;
    }

    public AtomicBoolean getActive() {
        return active;
    }
}