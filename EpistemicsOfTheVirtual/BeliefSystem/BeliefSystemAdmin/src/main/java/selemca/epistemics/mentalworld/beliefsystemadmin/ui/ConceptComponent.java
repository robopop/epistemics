package selemca.epistemics.mentalworld.beliefsystemadmin.ui;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.BeliefModelServiceUIHelper;

import java.util.Locale;

/**
 * Created by henrizwols on 10-11-15.
 */
public class ConceptComponent extends HorizontalLayout {
    private final TextField filter = new TextField();
    private final Grid conceptGrid = new Grid();
    private final Button newConcept = new Button("New concept");
    private final ConceptForm conceptForm;

    private final BeliefModelServiceUIHelper beliefModelServiceUIHelper;

    public ConceptComponent(BeliefModelServiceUIHelper beliefModelServiceUIHelper) {
        this.beliefModelServiceUIHelper = beliefModelServiceUIHelper;
        conceptForm = new ConceptForm(this, beliefModelServiceUIHelper);
        setLocale(Locale.ENGLISH);
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        newConcept.addClickListener(e -> conceptForm.editNew());

        filter.setInputPrompt("Filter concepts...");
        filter.addTextChangeListener(e -> refreshConcepts(e.getText()));

        conceptGrid.setContainerDataSource(new BeanItemContainer<>(Concept.class));
        conceptGrid.removeColumn("truthValue");
        conceptGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        conceptGrid.addSelectionListener(e
                -> conceptForm.edit((Concept) conceptGrid.getSelectedRow()));
        refreshConcepts();
    }

    private void buildLayout() {
        HorizontalLayout header = new HorizontalLayout(filter, newConcept);
        header.setWidth("100%");
        filter.setWidth("100%");
        header.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(header, conceptGrid);
        left.setSizeFull();
        conceptGrid.setSizeFull();
        left.setExpandRatio(conceptGrid, 1);

        addComponent(left);
        addComponent(conceptForm);
        setSizeFull();
        setExpandRatio(left, 1);
    }

    void refreshConcepts() {
        refreshConcepts(filter.getValue());
    }

    void refreshAll() {
        getUI().refreshAll();
    }

    private void refreshConcepts(String stringFilter) {
        conceptGrid.setContainerDataSource(new BeanItemContainer<>(
                Concept.class, beliefModelServiceUIHelper.findConcepts(stringFilter)));
        conceptForm.setVisible(false);
    }

    void deselectRows() {
        conceptGrid.select(null);
    }

    @Override
    public BeliefSystemUI getUI() {
        return (BeliefSystemUI) super.getUI();
    }
}
