package selemca.epistemics.mentalworld.engine.workingmemory;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

public class OptionalBag implements Bag {
    private Optional<Object> value = null;

    @Override
    public void clear() {
        value = Optional.empty();
    }

    @Override
    public void add(Object item) {
        if (item != null) {
            value = Optional.of(item);
        }
    }

    @Override
    public Iterator<Object> iterator() {
        return value.map(v -> Collections.singleton(v).iterator()).orElse(Collections.emptyIterator());
    }
}
