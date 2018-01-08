package selemca.epistemics.mentalworld.beliefsystem.graph;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class AssociationCsvFormat {
    private static final String[] HEADER = new String[] {"concept1", "concept2", "truthValue", "relation", "value"};
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withHeader(HEADER);

    public static String format(List<Association> associations, List<AssociationMeta> associationMetas) throws IOException {
        StringWriter writer = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT)) {
            for (Association association : associations) {
                List<AssociationMeta> metaForAssocitation = findMetaForAssocitation(association, associationMetas);
                if (metaForAssocitation.isEmpty()) {
                    printer.printRecord(getValues(association));
                } else {
                    for (AssociationMeta associationMeta : metaForAssocitation) {
                        printer.printRecord(getValues(association, associationMeta));
                    }
                }
            }
        }
        return writer.toString();
    }

    private static Object[] getValues(Association association) {
        Object[] result = new Object[3];
        result[0] = association.getConcept1().getName();
        result[1] = association.getConcept2().getName();
        result[2] = association.getTruthValue();
        return result;
    }

    private static Object[] getValues(Association association, AssociationMeta associationMeta) {
        Object[] result = new Object[5];
        result[0] = association.getConcept1().getName();
        result[1] = association.getConcept2().getName();
        result[2] = association.getTruthValue();
        result[3] = associationMeta.getRelation();
        result[4] = associationMeta.getValue();
        return result;
    }

    public static Association extractAssociation(CSVRecord csvRecord) {
        Concept concept1 = new Concept(getValue(csvRecord, "concept1"), 0.8);
        Concept concept2 = new Concept(getValue(csvRecord, "concept2"), 0.8);
        String truthValueStr = getValue(csvRecord, "truthValue");
        Double truthValue = parseDouble(truthValueStr);
        return new Association(concept1, concept2, truthValue);
    }

    public static AssociationMeta extractAssociationMeta(Association association, CSVRecord csvRecord) {
        if (csvRecord.size() >= 5) {
            String relation = getValue(csvRecord, "relation");
            String value = getValue(csvRecord, "value");
            return new AssociationMeta(association.getConcept1(), association.getConcept2(), relation, value);
        } else {
            return null;
        }
    }

    private static double parseDouble(String s) {
        try {
            return Double.valueOf(s);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(String.format("\"%s\" is not a number", s));
        }
    }

    private static String getValue(CSVRecord csvRecord, String key) {
        String value = csvRecord.get(key);
        if (value == null) {
            throw new IllegalArgumentException(String.format("%s is missing near line %s", key, csvRecord.getRecordNumber()));
        }
        return value;
    }

    private static List<AssociationMeta> findMetaForAssocitation(Association association, List<AssociationMeta> associationMetas) {
        List<AssociationMeta> match = new ArrayList<>();
        for (AssociationMeta associationMeta : associationMetas) {
            if (
                    (associationMeta.getConcept1().equals(association.getConcept1()) && associationMeta.getConcept2().equals(association.getConcept2()))
                            ||
                    (associationMeta.getConcept1().equals(association.getConcept2()) && associationMeta.getConcept2().equals(association.getConcept1()))
                ) {
                match.add(associationMeta);
            }
        }
        return match;
    }
}
