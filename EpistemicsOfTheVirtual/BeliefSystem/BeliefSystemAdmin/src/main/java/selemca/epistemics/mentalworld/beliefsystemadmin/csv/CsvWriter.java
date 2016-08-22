package selemca.epistemics.mentalworld.beliefsystemadmin.csv;

import com.vaadin.server.StreamResource;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.BeliefModelServiceUIHelper;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.AssociationUIObject;

import java.io.*;
import java.util.List;

/**
 * Created by henrizwols on 06-11-15.
 */
public class CsvWriter extends StreamResource {
    private static final String BELIEFSYSTEM_CSV_EXPORT_FILENAME = "Beliefsystem.csv";

    public CsvWriter(BeliefModelServiceUIHelper beliefModelServiceUIHelper) {
        super(new CsvStreamResource(beliefModelServiceUIHelper), BELIEFSYSTEM_CSV_EXPORT_FILENAME);
    }

    private static class CsvStreamResource implements StreamResource.StreamSource {
        private static final String[] HEADER = new String[] {"concept1", "concept2", "truthValue"};
        private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withHeader(HEADER);

        private final BeliefModelServiceUIHelper beliefModelServiceUIHelper;

        public CsvStreamResource(BeliefModelServiceUIHelper beliefModelServiceUIHelper) {
            this.beliefModelServiceUIHelper = beliefModelServiceUIHelper;
        }

        @Override
        public InputStream getStream() {
            try {
                List<AssociationUIObject> associations = beliefModelServiceUIHelper.findAssociations(null);
                return new ByteArrayInputStream(toCsv(associations).getBytes());
            } catch (IOException e) {
                return null;
            }
        }

        private String toCsv(List<AssociationUIObject> associations) throws IOException {
            StringWriter writer = new StringWriter();
            CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT);
            for (AssociationUIObject association : associations) {
                printer.printRecord(getValues(association));
            }
            printer.close();
            return writer.toString();
        }

        private Object[] getValues(AssociationUIObject association) {
            Object[] result = new Object[3];
            result[0] = association.getConcept1();
            result[1] = association.getConcept2();
            result[2] = Double.valueOf(association.getTruthValue());
            return result;
        }

    }
}
