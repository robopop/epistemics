/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.workingmemory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WorkingMemory {
    private final Map<AttributeKind,Bag> attributes = new HashMap<>();

    public Map<AttributeKind, Bag> getAttributes() {
        return attributes;
    }

    public <T> void set(AttributeKind<T> kind, T value) {
        kind.clear(this);
        kind.add(this, value);
    }

    public <T> void set(AttributeKind<T> kind, Iterable<T> value) {
        kind.clear(this);
        kind.addAll(this, value);
    }

    public <T> void add(AttributeKind<T> kind, T item) {
        kind.add(this, item);
    }

    public <T> T get(AttributeKind<T> kind) {
        Iterator<T> it = getAll(kind).iterator();
        if (!it.hasNext()) {
            throw new IllegalStateException("Not set");
        }
        T result = it.next();
        if (it.hasNext()) {
            throw new IllegalStateException("Ambiguous");
        }
        return result;
    }

    public <T> Iterable<T> getAll(AttributeKind<T> kind) {
        return kind.get(this);
    }
}
