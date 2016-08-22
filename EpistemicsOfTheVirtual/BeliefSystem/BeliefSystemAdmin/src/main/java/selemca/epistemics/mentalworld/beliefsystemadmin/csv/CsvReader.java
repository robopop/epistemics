package selemca.epistemics.mentalworld.beliefsystemadmin.csv;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.BeliefModelServiceUIHelper;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.AssociationUIObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by henrizwols on 08-11-15.
 */
public class CsvReader implements Upload.Receiver, Upload.SucceededListener {
    private static final String[] HEADER = new String[] {"concept1", "concept2", "truthValue"};
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withHeader();

    private final BeliefModelServiceUIHelper beliefModelServiceUIHelper;
    private File uploadFile;
    private OutputStream outputStream;
    private boolean wipeDb = false;

    public CsvReader(BeliefModelServiceUIHelper beliefModelServiceUIHelper) {
        this.beliefModelServiceUIHelper = beliefModelServiceUIHelper;
    }

    public boolean isWipeDb() {
        return wipeDb;
    }

    public void setWipeDb(boolean wipeDb) {
        this.wipeDb = wipeDb;
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        try {
            // Open the file for writing.
            uploadFile = File.createTempFile("Beliefsystem",".csv");
            outputStream = new FileOutputStream(uploadFile);
        } catch (IOException e) {
            new Notification("ACould not open file<br/>",
                    e.getMessage(),
                    Notification.Type.ERROR_MESSAGE)
                    .show(Page.getCurrent());
            return null;
        }
        return outputStream; // Return the output stream to write to
    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent event) {
        try {
            List<AssociationUIObject> associations = parseCsv();
            outputStream.close();
            beliefModelServiceUIHelper.wipeAll();
            for (AssociationUIObject association : associations) {
                beliefModelServiceUIHelper.save(association, null);
            }
        } catch (Exception e) {
            new Notification("BCould not open file<br/>",
                    e.getMessage(),
                    Notification.Type.ERROR_MESSAGE)
                    .show(Page.getCurrent());
        }
    }

    private List<AssociationUIObject> parseCsv() throws Exception {
        FileReader reader = new FileReader(uploadFile);
        CSVParser csvParser = new CSVParser(reader, CSV_FORMAT);
        Iterator<CSVRecord> recordIterator = csvParser.iterator();
        List<AssociationUIObject> associations = new ArrayList<>();
        while (recordIterator.hasNext()) {
            CSVRecord csvRecord = recordIterator.next();
            AssociationUIObject association = fromCsv(csvRecord);
            associations.add(association);
        }
        return associations;
    }

    private AssociationUIObject fromCsv(CSVRecord csvRecord) {
        AssociationUIObject association = new AssociationUIObject();
        association.setConcept1(csvRecord.get("concept1"));
        association.setConcept2(csvRecord.get("concept2"));
        String truthValueStr = csvRecord.get("truthValue");
        Double truthValue = Double.valueOf(truthValueStr);
        association.setTruthValue(truthValue);
        return association;
    }
}
