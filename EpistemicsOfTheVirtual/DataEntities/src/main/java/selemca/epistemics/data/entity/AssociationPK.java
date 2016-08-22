package selemca.epistemics.data.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Embedded key for an Association.
 */
@Embeddable
public class AssociationPK implements Serializable {
    @Basic(optional = false)
    @Column(nullable = false)
    private String concept1;
    @Basic(optional = false)
    @Column(nullable = false)
    private String concept2;

    protected AssociationPK() {
    }

    public AssociationPK(String concept1, String concept2) {
        this.concept1 = concept1;
        this.concept2 = concept2;
    }

    public String getConcept1() {
        return concept1;
    }

    public String getConcept2() {
        return concept2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssociationPK)) return false;

        AssociationPK that = (AssociationPK) o;

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

    public String toString() {
        return concept1 + " " + concept2;
    }
}
