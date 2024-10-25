package deltaalpha.crypto.jwglgl.api;

import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

@Data
public class EventService<T, I> {
    private List<IEventAcceptor<T, I>> listeners = new ArrayList<>();

    public void add(float priority, Consumer<ListenerEvent<T, I>> consumer) {
        add(new IEventAcceptor<>() {
            @Override
            public void accept(ListenerEvent<T, I> event) {
                consumer.accept(event);
            }

            @Override
            public float priority() {
                return priority;
            }
        });
    }

    public void add(IEventAcceptor<T, I> acceptor) {
        listeners.add(acceptor);
        listeners.sort(Comparator.comparingDouble(IEventAcceptor::priority));
    }

    public T publish(I input) {
        ListenerEvent<T, I> event = new ListenerEvent<>();
        event.setService(this);
        event.setInput(input);

        for(IEventAcceptor<T, I> listener : listeners) {
            listener.accept(event);

            if(event.isTerminate()) {
                break;
            }
        }

        return event.getValue();
    }
}