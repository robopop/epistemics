package selemca.epistemics.mentalworld.beliefsystemadmin.importexport;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.data.entity.ConceptMeta;
import selemca.epistemics.mentalworld.beliefsystem.repository.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ZipUploadReceiverFactory {
    private static final String CONCEPTS_CSV_EXPORT_FILENAME = "Concepts.csv";
    private static final String ASSOCIATIONS_CSV_EXPORT_FILENAME = "Associations.csv";
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withHeader().withCommentMarker('#');

    @Autowired
    private BeliefModelService beliefModelService;
    @Autowired
    private ConceptRepository conceptRepository;
    @Autowired
    private ConceptMetaRepository conceptMetaRepository;
    @Autowired
    private AssociationRepository associationRepository;
    @Autowired
    private AssociationMetaRepository associationMetaRepository;

    public ZipUploadReceiver createZipUploadReceiver() {
        return new ZipUploadReceiver(beliefModelService, conceptRepository, conceptMetaRepository, associationRepository, associationMetaRepository);
    }

    public static class ZipUploadReceiver implements Upload.Receiver, Upload.SucceededListener {
        private final BeliefModelService beliefModelService;
        private final ConceptRepository conceptRepository;
        private final ConceptMetaRepository conceptMetaRepository;
        private final AssociationRepository associationRepository;
        private final AssociationMetaRepository associationMetaRepository;

        private File uploadFile;
        private OutputStream outputStream;
        private boolean wipeDb = false;
        private boolean writeDb = false;

        public ZipUploadReceiver(BeliefModelService beliefModelService, ConceptRepository conceptRepository, ConceptMetaRepository conceptMetaRepository, AssociationRepository associationRepository, AssociationMetaRepository associationMetaRepository) {
            this.beliefModelService = beliefModelService;
            this.conceptRepository = conceptRepository;
            this.conceptMetaRepository = conceptMetaRepository;
            this.associationRepository = associationRepository;
            this.associationMetaRepository = associationMetaRepository;
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
                new Notification("Could not open file",
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
                importDbData(uploadFile);
            } catch (Exception e) {
                e.printStackTrace();
                new Notification("Could not read file",
                        e.getMessage(),
                        Notification.Type.ERROR_MESSAGE)
                        .show(Page.getCurrent());
            }
        }

        private void importDbData(File exportFile) throws IOException {
            // Test run
            writeDb = false;
            importZipFile(exportFile);

            // Wipe db is requested
            if (isWipeDb()) {
                beliefModelService.eraseAll();
            }
            // Now for real
            writeDb = true;
            importZipFile(exportFile);
        }

        private void importZipFile(File exportFile) throws IOException {
            try (FileInputStream fis = new FileInputStream(exportFile)) {
                try (BufferedInputStream bis = new BufferedInputStream(fis)) {
                    try (ZipInputStream zipInputStream = new ZipInputStream(bis)) {
                        ZipEntry zipEntry = null;
                        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                            readZipEntry(zipInputStream, zipEntry);
                            zipInputStream.closeEntry();
                        }
                    }
                }
            }
        }

        private void readZipEntry(ZipInputStream zipInputStream, ZipEntry zipEntry) throws IOException {
            String entryName = getZipEntryFileName(zipEntry);
            if (CONCEPTS_CSV_EXPORT_FILENAME.equals(entryName) || ASSOCIATIONS_CSV_EXPORT_FILENAME.equals(entryName)) {
                Reader reader = new InputStreamReader(zipInputStream, Charset.forName("utf-8"));
                CSVParser csvParser = new CSVParser(reader, CSV_FORMAT);
                if (CONCEPTS_CSV_EXPORT_FILENAME.equals(entryName)) {
                    importConcepts(csvParser);
                } else if (ASSOCIATIONS_CSV_EXPORT_FILENAME.equals(entryName)) {
                    importAssociations(csvParser);
                }
            }
        }

        private String getZipEntryFileName(ZipEntry zipEntry) {
            String entryName = "";
            if (!zipEntry.isDirectory()) {
                entryName = zipEntry.getName();
                int lastSlash = entryName.lastIndexOf('/');
                if (lastSlash >= 0) {
                    entryName = entryName.substring(lastSlash + 1);
                }
            }
            return entryName;
        }

        private void importConcepts(CSVParser csvParser) throws IOException {
            Iterator<CSVRecord> recordIterator = csvParser.iterator();
            while (recordIterator.hasNext()) {
                CSVRecord csvRecord = recordIterator.next();
                try {
                    Concept concept = ConceptCsvFormat.extractConcept(csvRecord);
                    ConceptMeta conceptMeta = ConceptCsvFormat.extractConceptMeta(csvRecord, concept);
                    if (writeDb) {
                        conceptRepository.save(concept);
                        if (conceptMeta != null) {
                            conceptMetaRepository.save(conceptMeta);
                        }
                    }
                } catch (Exception e) {
                    String message = String.format("Importing %s found a problem on line %s: %s", CONCEPTS_CSV_EXPORT_FILENAME, csvParser.getCurrentLineNumber(), e.getMessage());
                    if (writeDb) {
                        // Do not stop importing. Salvage what's left
                        Logger.getLogger(getClass().getSimpleName()).warning(message);
                    } else {
                        throw new IllegalArgumentException(message);
                    }
                }
            }
        }

        private void importAssociations(CSVParser csvParser) throws IOException {
            Iterator<CSVRecord> recordIterator = csvParser.iterator();
            while (recordIterator.hasNext()) {
                CSVRecord csvRecord = recordIterator.next();
                try {
                    Association association = AssociationCsvFormat.extractAssociation(csvRecord);
                    AssociationMeta associationMeta = AssociationCsvFormat.extractAssociationMeta(association, csvRecord);
                    if (writeDb) {
                        beliefModelService.fullSave(association);
                        if (associationMeta != null) {
                            associationMetaRepository.save(associationMeta);
                        }
                    }
                } catch (Exception e) {
                    String message = String.format("Importing %s found a problem on line %s: %s", ASSOCIATIONS_CSV_EXPORT_FILENAME, csvParser.getCurrentLineNumber(), e.getMessage());
                    if (writeDb) {
                        // Do not stop importing. Salvage what's left
                        Logger.getLogger(getClass().getSimpleName()).warning(message);
                    } else {
                        throw new IllegalArgumentException(message);
                    }
                }
            }
        }
    }
}
