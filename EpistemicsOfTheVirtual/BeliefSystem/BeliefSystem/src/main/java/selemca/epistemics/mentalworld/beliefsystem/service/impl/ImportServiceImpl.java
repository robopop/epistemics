package selemca.epistemics.mentalworld.beliefsystem.service.impl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.data.entity.ConceptMeta;
import selemca.epistemics.mentalworld.beliefsystem.graph.AssociationCsvFormat;
import selemca.epistemics.mentalworld.beliefsystem.graph.ConceptCsvFormat;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationMetaRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptMetaRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.beliefsystem.service.ImportService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static selemca.epistemics.mentalworld.beliefsystem.service.ExportService.ASSOCIATIONS_CSV_EXPORT_FILENAME;
import static selemca.epistemics.mentalworld.beliefsystem.service.ExportService.CONCEPTS_CSV_EXPORT_FILENAME;

@Component
public class ImportServiceImpl implements ImportService {
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withHeader().withCommentMarker('#');

    @Autowired
    private BeliefModelService beliefModelService;
    @Autowired
    private ConceptRepository conceptRepository;
    @Autowired
    private ConceptMetaRepository conceptMetaRepository;
    @Autowired
    private AssociationMetaRepository associationMetaRepository;

    @Override
    public void importDbData(File exportFile, boolean wipeDb) throws IOException {
        // Test run
        importZipFile(exportFile, false);

        // Wipe db is requested
        if (wipeDb) {
            beliefModelService.eraseAll();
        }

        // Now for real
        importZipFile(exportFile, true);
    }

    private void importZipFile(File exportFile, boolean writeDb) throws IOException {
        try (InputStream fis = new FileInputStream(exportFile)) {
            importZipFile(fis, writeDb);
        }
    }

    @Override
    public void importZipFile(InputStream input, boolean writeDb) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(input);
             ZipInputStream zipInputStream = new ZipInputStream(bis)
        ) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                readZipEntry(zipInputStream, zipEntry, writeDb);
                zipInputStream.closeEntry();
            }
        }
    }

    private void readZipEntry(ZipInputStream zipInputStream, ZipEntry zipEntry, boolean writeDb) throws IOException {
        String entryName = getZipEntryFileName(zipEntry);
        if (CONCEPTS_CSV_EXPORT_FILENAME.equals(entryName) || ASSOCIATIONS_CSV_EXPORT_FILENAME.equals(entryName)) {
            Reader reader = new InputStreamReader(zipInputStream, Charset.forName("utf-8"));
            CSVParser csvParser = new CSVParser(reader, CSV_FORMAT);
            if (CONCEPTS_CSV_EXPORT_FILENAME.equals(entryName)) {
                importConcepts(csvParser, writeDb);
            } else if (ASSOCIATIONS_CSV_EXPORT_FILENAME.equals(entryName)) {
                importAssociations(csvParser, writeDb);
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

    private void importConcepts(CSVParser csvParser, boolean writeDb) throws IOException {
        for (CSVRecord csvRecord : csvParser) {
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

    private void importAssociations(CSVParser csvParser, boolean writeDb) throws IOException {
        for (CSVRecord csvRecord : csvParser) {
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
