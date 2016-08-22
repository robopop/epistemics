package selemca.epistemics.mentalworld.beliefsystemadmin.ui;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.BeliefModelServiceUIHelper;

import java.util.Locale;

public class ConceptForm extends FormLayout {

    private final Button save = new Button("Save", this::save);
    private final Button cancel = new Button("Cancel", this::cancel);
    private final Button delete = new Button("Delete", this::delete);
    private final TextField name = new TextField("Name");
//    private final TextField truthValue = new TextField("Truth value");
    private final CheckBox isContext = new CheckBox("Context");

    private final ConceptComponent conceptComponent;
    private final BeliefModelServiceUIHelper beliefModelServiceUIHelper;

    Concept concept;

    BeanFieldGroup<Concept> formFieldBindings;

    public ConceptForm(ConceptComponent conceptComponent, BeliefModelServiceUIHelper beliefModelServiceUIHelper) {
        this.conceptComponent = conceptComponent;
        this.beliefModelServiceUIHelper = beliefModelServiceUIHelper;
        setLocale(Locale.ENGLISH);
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        /* Highlight primary actions.
         *
         * With Vaadin built-in styles you can highlight the primary save button
         * and give it a keyboard shortcut for a better UX.
         */
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        setVisible(false);
    }

    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);

        HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);
        actions.setSpacing(true);

        addComponents(actions, name, /*truthValue,*/ isContext);
    }

    public void save(Button.ClickEvent event) {
        try {
            // Commit the fields from UI to DAO
            formFieldBindings.commit();

            if (name.isEmpty()) {
                Notification.show("Concept name may not be empty", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if (concept.getTruthValue() <= 0 || concept.getTruthValue() >= 1) {
                Notification.show("Truth value must be between 0 and 1", Notification.Type.WARNING_MESSAGE);
                return;
            }

            // Save DAO to backend with direct synchronous service API
            beliefModelServiceUIHelper.save(concept, isContext.getValue());

            String msg = String.format("Saved '%s'.", concept.getName());
            Notification.show(msg, Notification.Type.TRAY_NOTIFICATION);
            conceptComponent.refreshConcepts();
        } catch (FieldGroup.CommitException e) {
            // Validation exceptions could be shown here
        }
    }

    public void cancel(Button.ClickEvent event) {
        // Place to call business logic.
        Notification.show("Cancelled", Notification.Type.TRAY_NOTIFICATION);
        setVisible(false);
        conceptComponent.deselectRows();
    }

    private void delete(Button.ClickEvent event) {
        beliefModelServiceUIHelper.delete(concept);
        String msg = String.format("Deleted '%s'.", concept.getName());
        Notification.show(msg, Notification.Type.TRAY_NOTIFICATION);
        conceptComponent.refreshAll();
    }

    void edit(Concept concept) {
        this.concept = concept;
        if(concept != null) {
            // Bind the properties of the contact POJO to fiels in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(concept, this);
            name.setReadOnly(true);
//            truthValue.focus();

            isContext.setValue(beliefModelServiceUIHelper.isContextConcept(concept));
        }
        delete.setEnabled(true);
        setVisible(concept != null);
    }

    void editNew() {
        concept = new Concept("", 0.8);
        formFieldBindings = BeanFieldGroup.bindFieldsBuffered(concept, this);
        name.setReadOnly(false);
        name.focus();
        delete.setEnabled(false);
        setVisible(true);
    }
}
