package deltaalpha.crypto.jwglgl.api;

public interface IEventAcceptor<T, I> {
    void accept(ListenerEvent<T, I> event);
    float priority();
}