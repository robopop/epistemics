package selemca.epistemics.mentalworld.beliefsystemadmin.ui;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.AssociationUIObject;
import selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess.BeliefModelServiceUIHelper;

import java.util.Arrays;
import java.util.Locale;

public class AssociationForm extends FormLayout {

    private final Button save = new Button("Save", this::save);
    private final Button cancel = new Button("Cancel", this::cancel);
    private final Button delete = new Button("Delete", this::delete);
    private final TextField concept1 = new TextField("Concept 1");
    private final TextField concept2 = new TextField("Concept 2");
    private final TextField truthValue = new TextField("Truth value");
    private final ComboBox relationType = new ComboBox("Relation type", Arrays.asList(RelationType.values()));

    private final AssociationComponent associationComponent;
    private final BeliefModelServiceUIHelper beliefModelServiceUIHelper;

    AssociationUIObject association;

    BeanFieldGroup<AssociationUIObject> formFieldBindings;

    public AssociationForm(AssociationComponent associationComponent, BeliefModelServiceUIHelper beliefModelServiceUIHelper) {
        this.associationComponent = associationComponent;
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

        addComponents(actions, concept1, concept2, truthValue, relationType);
    }

    public void save(Button.ClickEvent event) {
        try {
            // Commit the fields from UI to DAO
            formFieldBindings.commit();

            if (association.getConcept1().isEmpty() || association.getConcept2().isEmpty()) {
                Notification.show("Concept names may not be empty", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if (association.getTruthValue() <= 0 || association.getTruthValue() >= 1) {
                Notification.show("Truth value must be between 0 and 1", Notification.Type.WARNING_MESSAGE);
                return;
            }

            RelationType relationType = (RelationType) this.relationType.getValue();
            String relationTypeDBValue = relationType == null ? null : relationType.dbValue;

            // Save DAO to backend with direct synchronous service API
            beliefModelServiceUIHelper.save(association, relationTypeDBValue);

            String msg = String.format("Saved '%s-%s (%s)'.",
                    association.getConcept1(),association.getConcept2(), association.getTruthValue());
            Notification.show(msg, Notification.Type.TRAY_NOTIFICATION);
            associationComponent.refreshAssociations();
        } catch (FieldGroup.CommitException e) {
            // Validation exceptions could be shown here
        }
    }

    public void cancel(Button.ClickEvent event) {
        // Place to call business logic.
        Notification.show("Cancelled", Notification.Type.TRAY_NOTIFICATION);
        setVisible(false);
        associationComponent.deselectRows();
    }

    private void delete(Button.ClickEvent event) {
        beliefModelServiceUIHelper.delete(association);
        String msg = String.format("Deleted '%s-%s (%s)'.",
                association.getConcept1(),association.getConcept2(), association.getTruthValue());
        Notification.show(msg, Notification.Type.TRAY_NOTIFICATION);
        associationComponent.refreshAssociations();
    }

    void edit(AssociationUIObject associationUIObject) {
        this.association = associationUIObject;
        if(association != null) {
            // Bind the properties of the contact POJO to fiels in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(association, this);
            concept1.setReadOnly(true);
            concept2.setReadOnly(true);
            truthValue.focus();

            String relationTypeDbValue = beliefModelServiceUIHelper.findRelationType(association);
            relationType.setValue(RelationType.fromDbValue(relationTypeDbValue));
        }
        delete.setEnabled(true);
        setVisible(association != null);
    }

    void editNew() {
        association = new AssociationUIObject();
        association.setConcept1("");
        association.setConcept2("");
        formFieldBindings = BeanFieldGroup.bindFieldsBuffered(association, this);
        concept1.setReadOnly(false);
        concept2.setReadOnly(false);
        concept1.focus();
        delete.setEnabled(false);
        setVisible(true);
    }

    public static enum RelationType {
        LITERAL("Literal", "l"), FIGURATIVE("Figurative", "f")
        ;
        private final String displayName;
        private final String dbValue;

        RelationType(String displayName, String dbValue) {
            this.displayName = displayName;
            this.dbValue = dbValue;
        }

        static RelationType fromDbValue(String dbValue) {
            RelationType result = null;
            for (RelationType relationType : RelationType.values()) {
                if (relationType.dbValue.equals(dbValue)) {
                    result = relationType;
                    break;
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

}
