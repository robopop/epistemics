package selemca.epistemics.data.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Metadata for a concept. Defined as a tripple: concept - relation - value
 * There can be any number of metadata items for a concept.
 * Metadata is identified by an autonumber.
 */
@Entity
@Table
@NamedQuery(name = "ConceptMeta.findByConcept", query = "SELECT m FROM ConceptMeta m WHERE m.concept = ?1")
public class ConceptMeta implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(referencedColumnName = "NAME", nullable = false)
    @ManyToOne(optional = false)
    private Concept concept;

    @Column(nullable = false)
    private String relation;

    @Column(nullable = false)
    private String value;

    private ConceptMeta() {
    }

    public ConceptMeta(Concept concept, String relation, String value) {
        this.concept = concept;
        this.relation = relation;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getRelation() {
        return relation;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return String.format("%s %s %s", concept, relation, value);
    }
}
