package selemca.epistemics.mentalworld.engine.workingmemory;

import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.accept.Engine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static selemca.epistemics.mentalworld.engine.workingmemory.Cardinality.*;

public class AttributeKind<T> {
    // WorkingMemory
    public static final AttributeKind<Engine> ENGINE_SETTINGS = create(Engine.class);
    public static final AttributeKind<String> OBSERVATION_FEATURES = create(String.class, SET);
    public static final AttributeKind<CategoryMatch> CATEGORY_MATCH = create(CategoryMatch.class);
    public static final AttributeKind<Concept> NEW_CONTEXT = create(Concept.class);

    // BelieverDeviationDeriverNode
    public static final AttributeKind<Concept> WILLING_TO_DEVIATE_CONTRIBUTORS = create(Concept.class, SET);
    public static final AttributeKind<Concept> UNWILLING_TO_DEVIATE_CONTRIBUTORS = create(Concept.class, SET);

    // CategoryMatchDeriveNode
    public static final AttributeKind<String> PRECLUDE_CONCEPTS = create(String.class, SET);

    // SetLiteral, SetFictional, InsecurityDeriverNode, ChangeConceptDeriverNode
    public static final AttributeKind<Association> ASSOCIATION = create(Association.class);
    public static final AttributeKind<Boolean> IS_METAPHOR = create(Boolean.class);

    // EpistemicAppraisalDeriverNode
    public static final AttributeKind<Concept> CATEGORY = create(Concept.class);
    public static final AttributeKind<Association> REALISTIC_CONTRIBUTIONS = create(Association.class, SET);
    public static final AttributeKind<Association> UNREALISTIC_CONTRIBUTIONS = create(Association.class, SET);

    // IntegratorDeviationDeriverNode
    public static final AttributeKind<Concept> CONCEPT = create(Concept.class);
    public static final AttributeKind<Concept> CONTRIBUTOR = create(Concept.class);

    private final Class<T> type;
    private final Cardinality cardinality;

    private AttributeKind(Class<T> type, Cardinality cardinality) {
        this.type = type;
        this.cardinality = cardinality;
    }

    public static  <T> AttributeKind<T> create(Class<T> type) {
        return create(type, SINGLE);
    }

    public static  <T> AttributeKind<T> create(Class<T> type, Cardinality cardinality) {
        return new AttributeKind<T>(type, cardinality);
    }

    public Iterable<T> get(WorkingMemory workingMemory) {
        Iterable<?> result = workingMemory.getAttributes().get(this);
        return result == null ? Collections.emptySet() : cast(result);
    }

    protected <S> Iterable<T> cast(Iterable<S> source) {
        return () -> StreamSupport.stream(source.spliterator(), false).filter(type::isInstance).map(type::cast).iterator();
    }

    public void add(WorkingMemory workingMemory, T item) {
        Bag values = workingMemory.getAttributes().get(this);
        if (values == null) {
            switch (cardinality) {
                case SINGLE:
                    values = new OptionalBag();
                    break;
                case BAG:
                    values = new CollectionBag(false);
                    break;
                case SET:
                    values = new CollectionBag(true);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown cardinality: " + cardinality);
            }
            workingMemory.getAttributes().put(this, values);
        }
        values.add(item);
    }

    public void addAll(WorkingMemory workingMemory, Iterable<? extends T> items) {
        items.forEach(item -> this.add(workingMemory, item));
    }

    public void clear(WorkingMemory workingMemory) {
        Bag values = workingMemory.getAttributes().get(this);
        if (values != null) {
            values.clear();
        }
    }

    public static long size(Iterable<?> it) {
        return StreamSupport.stream(it.spliterator(), false).count();
    }

    public static <E> Set<E> toSet(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toSet());
    }
}
