package selemca.epistemics.mentalworld.engine.workingmemory;

import java.util.Iterator;

public interface Bag extends Iterable<Object> {
    void clear();
    void add(Object item);
    Iterator<Object> iterator();
}
