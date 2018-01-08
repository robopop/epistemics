package selemca.epistemics.mentalworld.beliefsystem.service;

import java.io.File;
import java.io.IOException;

public interface ExportService {
    String CONCEPTS_CSV_EXPORT_FILENAME = "Concepts.csv";
    String ASSOCIATIONS_CSV_EXPORT_FILENAME = "Associations.csv";

    void exportDbData(File exportFile) throws IOException;
}
