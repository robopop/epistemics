package selemca.epistemics.data.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Relation between 2 concepts in a concept-graph.
 * A relation is undirected, is identified by its 2 concepts and had a truth value.
 * The equals and hashCode are overwritten to work on only the identifier(s).
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Association.findByConcept1", query = "SELECT a FROM Association a WHERE a.concept1 = ?1"),
        @NamedQuery(name = "Association.findByConcept2", query = "SELECT a FROM Association a WHERE a.concept2 = ?1"),
})
@Table
public class Association implements Serializable {

    @EmbeddedId
    private AssociationPK id;

    @JoinColumn(name = "CONCEPT1", referencedColumnName = "name", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Concept concept1;
    @JoinColumn(name = "CONCEPT2", referencedColumnName = "name", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Concept concept2;

    @Column(nullable = false)
    private double truthValue;

    protected Association() {
    }

    public Association(Concept concept1, Concept concept2, double truthValue) {
        this.concept1 = concept1;
        this.concept2 = concept2;
        this.truthValue = truthValue;
    }

    @PrePersist
    private void prePersiste() {
        if (id == null) {
            id = new AssociationPK(concept1.getName(), concept2.getName());
        }
    }

    public Concept getConcept1() {
        return concept1;
    }

    public void setConcept1(Concept concept1) {
        this.concept1 = concept1;
    }

    public Concept getConcept2() {
        return concept2;
    }

    public void setConcept2(Concept concept2) {
        this.concept2 = concept2;
    }

    public double getTruthValue() {
        return truthValue;
    }

    public void setTruthValue(double truthValue) {
        this.truthValue = truthValue;
    }

    public Concept getOtherConcept(Concept concept) {
        if (concept.equals(concept1)) {
            return concept2;
        }
        if (concept.equals(concept2)) {
            return concept1;
        }
        throw new IllegalArgumentException(String.format("Concept %s not part of association %s", concept, this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Association)) return false;

        Association that = (Association) o;

        if (!concept1.equals(that.concept1)) return false;
        if (!concept2.equals(that.concept2)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = concept1.hashCode();
        result = 31 * result + concept2.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s -%.2f- %s", concept1, truthValue, concept2);
    }
}
