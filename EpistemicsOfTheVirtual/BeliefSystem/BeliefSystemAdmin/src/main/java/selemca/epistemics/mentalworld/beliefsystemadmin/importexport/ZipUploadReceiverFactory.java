package selemca.epistemics.mentalworld.beliefsystemadmin.importexport;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.beliefsystem.graph.Importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class ZipUploadReceiverFactory {

    @Autowired
    private Importer importer;

    public ZipUploadReceiver createZipUploadReceiver() {
        return new ZipUploadReceiver();
    }

    public class ZipUploadReceiver implements Upload.Receiver, Upload.SucceededListener {
        private File uploadFile;
        private OutputStream outputStream;
        private boolean wipeDb = false;

        public ZipUploadReceiver() {
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
                importer.importDbData(uploadFile, isWipeDb());
            } catch (Exception e) {
                e.printStackTrace();
                new Notification("Could not read file",
                        e.getMessage(),
                        Notification.Type.ERROR_MESSAGE)
                        .show(Page.getCurrent());
            }
        }
    }
}
