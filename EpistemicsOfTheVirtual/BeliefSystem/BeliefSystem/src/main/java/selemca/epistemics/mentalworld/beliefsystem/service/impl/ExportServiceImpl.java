package selemca.epistemics.mentalworld.beliefsystem.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.data.entity.ConceptMeta;
import selemca.epistemics.mentalworld.beliefsystem.graph.AssociationCsvFormat;
import selemca.epistemics.mentalworld.beliefsystem.graph.ConceptCsvFormat;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationMetaRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptMetaRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.beliefsystem.service.ExportService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ExportServiceImpl implements ExportService {

    @Autowired
    private ConceptRepository conceptRepository;
    @Autowired
    private ConceptMetaRepository conceptMetaRepository;
    @Autowired
    private AssociationRepository associationRepository;
    @Autowired
    private AssociationMetaRepository associationMetaRepository;

    @Override
    public void exportDbData(File exportFile) throws IOException {
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
