package selemca.epistemics.mentalworld.beliefsystemadmin.importexport;

import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.beliefsystem.service.ExportService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ZipExportStreamResourceFactory {
    private static final String BELIEFSYSTEM_CSV_EXPORT_FILENAME = "Beliefsystem.zip";

    @Autowired
    private ExportService exportService;

    public StreamResource createZipExportStreamResource() {
        ZipExportStreamSource zipExportStreamSource = new ZipExportStreamSource();
        return new StreamResource(zipExportStreamSource, BELIEFSYSTEM_CSV_EXPORT_FILENAME);
    }

    private class ZipExportStreamSource implements StreamResource.StreamSource {

        public ZipExportStreamSource() {
        }

        @Override
        public InputStream getStream() {
            try {
                File tempFile = File.createTempFile("Beliefsystem", ".zip");
                exportService.exportDbData(tempFile);
                return new FileInputStream(tempFile);
            } catch (IOException e) {
                new Notification("Could not export file",
                        e.getMessage(),
                        Notification.Type.ERROR_MESSAGE)
                        .show(Page.getCurrent());
                return null;
            }
        }
    }
}
