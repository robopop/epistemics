package selemca.epistemics.mentalworld.beliefsystemadmin.importexport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.data.entity.ConceptMeta;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by henrizwols on 09-11-15.
 */
public class ConceptCsvFormat {
    private static final String[] HEADER = new String[] {"name", "relation", "value"};
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withHeader(HEADER);

    public static String format(List<Concept> concepts, List<ConceptMeta> conceptMetas) throws IOException {
        StringWriter writer = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT)) {
            for (Concept concept : concepts) {
                List<ConceptMeta> metaForConcept = findMetaForConcept(concept, conceptMetas);
                if (metaForConcept.isEmpty()) {
                    printer.printRecord(getValues(concept));
                } else {
                    for (ConceptMeta conceptMeta : metaForConcept) {
                        printer.printRecord(getValues(concept, conceptMeta));
                    }
                }
            }
        }
        return writer.toString();
    }

    private static Object[] getValues(Concept concept) {
        Object[] result = new Object[1];
        result[0] = concept.getName();
        return result;
    }

    private static Object[] getValues(Concept concept, ConceptMeta conceptMeta) {
        Object[] result = new Object[3];
        result[0] = concept.getName();
        result[1] = conceptMeta.getRelation();
        result[2] = conceptMeta.getValue();
        return result;
    }

    private static List<ConceptMeta> findMetaForConcept(Concept concept, List<ConceptMeta> conceptMetas) {
        List<ConceptMeta> match = new ArrayList<>();
        for (ConceptMeta conceptMeta : conceptMetas) {
            if (conceptMeta.getConcept().equals(concept)) {
                    match.add(conceptMeta);
            }
        }
        return match;
    }

    public static Concept extractConcept(CSVRecord csvRecord) {
        return new Concept(getValue(csvRecord, "name"), 0.8);
    }

    public static ConceptMeta extractConceptMeta(CSVRecord csvRecord, Concept concept) {
        if (csvRecord.size() >= 2) {
            String relation = getValue(csvRecord, "relation");
            String value = getValue(csvRecord, "value");
            return new ConceptMeta(concept, relation, value);
        } else {
            return null;
        }
    }

    private static String getValue(CSVRecord csvRecord, String key) {
        String value = csvRecord.get(key);
        if (value == null) {
            throw new IllegalArgumentException(String.format("%s is missing near line %s", key, csvRecord.getRecordNumber()));
        }
        return value;
    }
}

