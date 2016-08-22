package selemca.epistemics.mentalworld.beliefsystemadmin.ui;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.AssociationUIObject;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.BeliefModelServiceUIHelper;

import java.util.Locale;

/**
 * Created by henrizwols on 10-11-15.
 */
public class AssociationComponent extends HorizontalLayout {
    private final TextField filter = new TextField();
    private final Grid associationGrid = new Grid();
    private final Button newAssociation = new Button("New association");
    private final AssociationForm associationForm;

    private final BeliefModelServiceUIHelper beliefModelServiceUIHelper;

    public AssociationComponent(BeliefModelServiceUIHelper beliefModelServiceUIHelper) {
        this.beliefModelServiceUIHelper = beliefModelServiceUIHelper;
        associationForm = new AssociationForm(this, beliefModelServiceUIHelper);
        setLocale(Locale.ENGLISH);
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        newAssociation.addClickListener(e -> associationForm.editNew());

        filter.setInputPrompt("Filter associations...");
        filter.addTextChangeListener(e -> refreshAssociations(e.getText()));

        associationGrid.setContainerDataSource(new BeanItemContainer<>(AssociationUIObject.class));
        associationGrid.setColumnOrder("concept1", "concept2", "truthValue");
        associationGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        associationGrid.addSelectionListener(e
                -> associationForm.edit((AssociationUIObject) associationGrid.getSelectedRow()));
        refreshAssociations();
    }

    private void buildLayout() {
        HorizontalLayout header = new HorizontalLayout(filter, newAssociation);
        header.setWidth("100%");
        filter.setWidth("100%");
        header.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(header, associationGrid);
        left.setSizeFull();
        associationGrid.setSizeFull();
        left.setExpandRatio(associationGrid, 1);

        addComponent(left);
        addComponent(associationForm);
        setSizeFull();
        setExpandRatio(left, 1);
    }

    void refreshAssociations() {
        refreshAssociations(filter.getValue());
    }

    private void refreshAssociations(String stringFilter) {
        associationGrid.setContainerDataSource(new BeanItemContainer<>(
                AssociationUIObject.class, beliefModelServiceUIHelper.findAssociations(stringFilter)));
        associationForm.setVisible(false);
    }

    void deselectRows() {
        associationGrid.select(null);
    }
}
