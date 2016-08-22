package selemca.epistemics.mentalworld.beliefsystemadmin.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.BeliefModelServiceUIHelper;

import javax.servlet.annotation.WebServlet;
import java.util.Locale;

@Title("Belief system")
@Theme("valo")
public class BeliefSystemUI extends UI {
    private final BeliefModelServiceUIHelper service = new BeliefModelServiceUIHelper(VaadinServlet.getCurrent().getServletContext());
    private final ConceptComponent conceptComponent = new ConceptComponent(service);
    private final AssociationComponent associationComponent = new AssociationComponent(service);
    private final ImportExportComponent importExportComponent = new ImportExportComponent(service);
    private final TabSheet tabsheet = new TabSheet();

    @Override
    protected void init(VaadinRequest request) {
        setLocale(Locale.ENGLISH);
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {

    }

    private void buildLayout() {
        tabsheet.addTab(conceptComponent, "Concepts");
        tabsheet.addTab(associationComponent, "Associations");
        tabsheet.addTab(importExportComponent, "Import/Export");
        tabsheet.setSizeFull();
        setContent(tabsheet);
    }

    public void refreshAll() {
        conceptComponent.refreshConcepts();
        associationComponent.refreshAssociations();
    }

    /*  Deployed as a Servlet or Portlet.
     *
     *  You can specify additional servlet parameters like the URI and UI
     *  class name and turn on production mode when you have finished developing the application.
     */
    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = BeliefSystemUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
