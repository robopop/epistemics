package selemca.epistemics.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Ownstate property.
 * An ownstate is identified by its property name and had a string value.
 * The equals and hashCode are overwritten to work on only the identifier(s).
 */
@Entity
@Table
public class OwnState {
    @Id
    private String property;

    @Column(nullable = false)
    private String value;

    private OwnState() {
    }

    public OwnState(String property) {
        this.property = property;
    }

    public OwnState(String property, String value) {
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OwnState)) return false;

        OwnState that = (OwnState) o;

        if (!property.equals(that.property)) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = property.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s=%s", property, value);
    }
}
