package selemca.epistemics.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Setting for the Engine.
 * A concept is identified by its name and has a value.
 * The equals and hashCode are overwritten to work on only the identifier(s).
 */
@Entity
@Table
public class Setting implements Serializable {
    @Id
    private String name;
    @Column(nullable = false)
    private double value;

    protected Setting() {
    }

    public Setting(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String deptName) {
        this.name = deptName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Setting)) return false;

        Setting concept = (Setting) o;

        if (!name.equals(concept.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
//        return String.format("%s(%.2f)", name, value);
        return String.format("%s", name);
    }

}
