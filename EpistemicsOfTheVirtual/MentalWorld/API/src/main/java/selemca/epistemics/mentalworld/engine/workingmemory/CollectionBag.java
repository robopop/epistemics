package selemca.epistemics.mentalworld.engine.workingmemory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class CollectionBag implements Bag {
    private final boolean unique;
    private Collection<Object> collection;

    public CollectionBag() {
        this(false);
    }

    public CollectionBag(boolean unique) {
        this.unique = unique;
        clear();
    }

    @Override
    public void clear() {
        if (unique) {
            this.collection = new HashSet<>();
        } else {
            this.collection = new ArrayList<>();
        }
    }

    @Override
    public void add(Object item) {
        collection.add(item);
    }

    @Override
    public Iterator<Object> iterator() {
        return collection.iterator();
    }
}
