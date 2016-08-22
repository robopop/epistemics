package selemca.epistemics.data.entity;

import javax.persistence.*;

/**
 * Metadata for an association. Defined as a quadruple: concept1 - concept2 - relation - value
 * There can be any number of metadata items for an association.
 * Metadata is identified by an autonumber.
 */
@Entity
@Table
public class AssociationMeta {
    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(name = "CONCEPT1", referencedColumnName = "NAME", nullable = false)
    @ManyToOne(optional = false)
    private Concept concept1;

    @JoinColumn(name = "CONCEPT2", referencedColumnName = "NAME", nullable = false)
    @ManyToOne(optional = false)
    private Concept concept2;

    @Column(nullable = false)
    private String relation;

    @Column(nullable = false)
    private String value;

    private AssociationMeta() {
    }

    public AssociationMeta(Concept concept1, Concept concept2, String relation, String value) {
        this.concept1 = concept1;
        this.concept2 = concept2;
        this.relation = relation;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public Concept getConcept1() {
        return concept1;
    }

    public Concept getConcept2() {
        return concept2;
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
        return String.format("%s %s %s %s", concept1, concept2, relation, value);
    }
}
