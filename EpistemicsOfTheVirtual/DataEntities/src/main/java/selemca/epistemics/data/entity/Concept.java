package selemca.epistemics.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Node in a concept-graph.
 * A concept is identified by its name and had a truth value.
 * The equals and hashCode are overwritten to work on only the identifier(s).
 */
@Entity
@Table
public class Concept implements Serializable {
    @Id
    private String name;
    @Column(nullable = false)
    private double truthValue;

    protected Concept() {
    }

    public Concept(String name, double truthValue) {
        this.name = name;
        this.truthValue = truthValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String deptName) {
        this.name = deptName;
    }

    public double getTruthValue() {
        return truthValue;
    }

    public void setTruthValue(double truthValue) {
        this.truthValue = truthValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Concept)) return false;

        Concept concept = (Concept) o;

        if (!name.equals(concept.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
//        return String.format("%s(%.2f)", name, truthValue);
        return String.format("%s", name);
    }

}
