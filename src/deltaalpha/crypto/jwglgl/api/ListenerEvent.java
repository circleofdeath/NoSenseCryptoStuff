package deltaalpha.crypto.jwglgl.api;

import lombok.Data;

@Data
public class ListenerEvent<T, I> {
    private EventService<T, I> service;
    private boolean terminate;
    private T value;
    private I input;

    public void returnValue(T value) {
        setValue(value);
        setTerminate(true);
    }
}