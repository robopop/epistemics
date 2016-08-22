package selemca.epistemics.mentalworld.beliefsystemadmin.ui;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.BeliefModelServiceUIHelper;
import selemca.epistemics.mentalworld.beliefsystemadmin.importexport.ZipUploadReceiverFactory;

import java.util.Locale;

/**
 * Created by henrizwols on 10-11-15.
 */
public class ImportExportComponent extends HorizontalLayout {
    private final BeliefModelServiceUIHelper beliefModelServiceUIHelper;

    private final Button downloadAssocations = new Button("Download belief system as ZIP file");
    private Upload uploadAssocations;
    private final CheckBox wipeDbCheckBox = new CheckBox("Replace Beliefsystem with file");

    public ImportExportComponent(BeliefModelServiceUIHelper beliefModelServiceUIHelper) {
        this.beliefModelServiceUIHelper = beliefModelServiceUIHelper;
        setLocale(Locale.ENGLISH);
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        StreamResource myResource = beliefModelServiceUIHelper.getZipExportStreamResourceFactory().createZipExportStreamResource();
        FileDownloader fileDownloader = new FileDownloader(myResource);
        fileDownloader.extend(downloadAssocations);

        ZipUploadReceiverFactory.ZipUploadReceiver zipUploadReceiver = beliefModelServiceUIHelper.getZipUploadReceiverFactory().createZipUploadReceiver();
        uploadAssocations = new Upload("Upload from ZIP file", zipUploadReceiver);
        uploadAssocations.setButtonCaption("Start Upload");
        uploadAssocations.addSucceededListener((Upload.SucceededListener) event -> {
            zipUploadReceiver.uploadSucceeded(event);
            getUI().refreshAll();
            Notification.show("Upload complete", Notification.Type.TRAY_NOTIFICATION);
        });
        wipeDbCheckBox.addValueChangeListener(e -> zipUploadReceiver.setWipeDb(wipeDbCheckBox.getValue()));
    }

    private void buildLayout() {
        VerticalLayout uploadLayout = new VerticalLayout(uploadAssocations, wipeDbCheckBox);
        addComponent(downloadAssocations);
        addComponent(uploadLayout);
        setSizeFull();
    }

    @Override
    public BeliefSystemUI getUI() {
        return (BeliefSystemUI) super.getUI();
    }
}
