package selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess;

/**
 * Created by henrizwols on 06-11-15.
 */
public class AssociationUIObject {
    private String concept1;
    private String concept2;
    private double truthValue;

    public String getConcept1() {
        return concept1;
    }

    public void setConcept1(String concept1) {
        this.concept1 = concept1;
    }

    public String getConcept2() {
        return concept2;
    }

    public void setConcept2(String concept2) {
        this.concept2 = concept2;
    }

    public double getTruthValue() {
        return truthValue;
    }

    public void setTruthValue(double truthValue) {
        this.truthValue = truthValue;
    }
}
