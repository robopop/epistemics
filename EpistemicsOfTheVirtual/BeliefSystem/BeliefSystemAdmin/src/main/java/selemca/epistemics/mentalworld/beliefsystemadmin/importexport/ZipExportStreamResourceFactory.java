package selemca.epistemics.mentalworld.beliefsystemadmin.importexport;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.data.entity.ConceptMeta;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationMetaRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptMetaRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipExportStreamResourceFactory {
    private static final String BELIEFSYSTEM_CSV_EXPORT_FILENAME = "Beliefsystem.zip";
    private static final String CONCEPTS_CSV_EXPORT_FILENAME = "Concepts.csv";
    private static final String ASSOCIATIONS_CSV_EXPORT_FILENAME = "Associations.csv";

    @Autowired
    private ConceptRepository conceptRepository;
    @Autowired
    private ConceptMetaRepository conceptMetaRepository;
    @Autowired
    private AssociationRepository associationRepository;
    @Autowired
    private AssociationMetaRepository associationMetaRepository;

    public StreamResource createZipExportStreamResource() {
        ZipExportStreamSource zipExportStreamSource = new ZipExportStreamSource(conceptRepository, conceptMetaRepository, associationRepository, associationMetaRepository);
        return new StreamResource(zipExportStreamSource, BELIEFSYSTEM_CSV_EXPORT_FILENAME);
    }

    private static class ZipExportStreamSource implements StreamResource.StreamSource {
        private final ConceptRepository conceptRepository;
        private final ConceptMetaRepository conceptMetaRepository;
        private final AssociationRepository associationRepository;
        private final AssociationMetaRepository associationMetaRepository;

        public ZipExportStreamSource(ConceptRepository conceptRepository, ConceptMetaRepository conceptMetaRepository, AssociationRepository associationRepository, AssociationMetaRepository associationMetaRepository) {
            this.conceptRepository = conceptRepository;
            this.conceptMetaRepository = conceptMetaRepository;
            this.associationRepository = associationRepository;
            this.associationMetaRepository = associationMetaRepository;
        }

        @Override
        public InputStream getStream() {
            try {
                File tempFile = File.createTempFile("Beliefsystem", ".zip");
                exportDbData(tempFile);
                return new FileInputStream(tempFile);
            } catch (IOException e) {
                new Notification("Could not export file",
                        e.getMessage(),
                        Notification.Type.ERROR_MESSAGE)
                        .show(Page.getCurrent());
                return null;
            }
        }


        private void exportDbData(File exportFile) throws IOException {
            try (FileOutputStream fos = new FileOutputStream(exportFile)) {
                try (BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                    try (ZipOutputStream zipOutputStream = new ZipOutputStream(bos)) {
                        zipOutputStream.putNextEntry(new ZipEntry(CONCEPTS_CSV_EXPORT_FILENAME));
                        exportConcepts(zipOutputStream);
                        zipOutputStream.closeEntry();
                        zipOutputStream.putNextEntry(new ZipEntry(ASSOCIATIONS_CSV_EXPORT_FILENAME));
                        exportAssociations(zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                }
            }
        }

        private void exportConcepts(OutputStream outputStream) throws IOException {
            List<Concept> concepts = conceptRepository.findAll();
            List<ConceptMeta> conceptMetas = conceptMetaRepository.findAll();
            String format = ConceptCsvFormat.format(concepts, conceptMetas);
            outputStream.write(format.getBytes());
        }

        private void exportAssociations(OutputStream outputStream) throws IOException {
            List<Association> associations = associationRepository.findAll();
            List<AssociationMeta> associationMetas = associationMetaRepository.findAll();
            String format = AssociationCsvFormat.format(associations, associationMetas);
            outputStream.write(format.getBytes());
        }
    }

}
