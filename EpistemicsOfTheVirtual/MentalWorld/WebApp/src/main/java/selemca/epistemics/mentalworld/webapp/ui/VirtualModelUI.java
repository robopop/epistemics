package selemca.epistemics.mentalworld.webapp.ui;

import java.io.Serializable;
import java.util.Optional;
import java.util.SortedSet;

import com.vaadin.annotations.Push;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.*;
import org.apache.commons.configuration.Configuration;
import selemca.epistemics.data.entity.Concept;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.webapp.wordnet.WordnetAccess;

@SuppressWarnings("serial")
@Title("Offer observation")
@Theme("valo")
@Push
public class VirtualModelUI extends UI {
	private static final String DEBUG_ON_SCREEN_PROPERTY = "logging.debugOnScreen";
	private static final String DEBUG_IN_LOG_PROPERTY = "logging.debugInLog";

	VirtualModelComponent virtualModelComponent = new VirtualModelComponent();

	private BeliefModelService beliefModelService;
	private WordnetAccess wordnetAccess;
	private MentalWorldEngine virtualModelEngine;
	private Configuration applicationSettings;
    private ConceptRepository conceptRepository;
	
	private final MentalWorldEngine.Logger logAreaLogger = new SynchronousLogAreaLogger();

	@Override
	protected void init(VaadinRequest request) {
		SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		beliefModelService = (BeliefModelService) helper.getBean("beliefModelService");
		wordnetAccess = (WordnetAccess) helper.getBean("wordnetAccess");
		virtualModelEngine = (MentalWorldEngine) helper.getBean("mentalWorldEngine");
		applicationSettings = (Configuration) helper.getBean("applicationSettings");
        conceptRepository = (ConceptRepository) helper.getBean("conceptRepository");

		ObservationComponent observationComponent = virtualModelComponent.getObservationComponent();
		fillBeliefListSelect(observationComponent.getBeliefListSelect());
		observationComponent.getWordnetCheckBox().setEnabled(false);
		new Thread(new UpdateWordnetComboboxRunnable(observationComponent.getWordnetComboBox(), observationComponent.getWordnetCheckBox())).start();

		OfferObservationButtonListener buttonListener = new OfferObservationButtonListener(virtualModelEngine, this, observationComponent, logAreaLogger);
		virtualModelComponent.getOfferObservationButton().addClickListener(buttonListener);

		virtualModelComponent.setContextConcepts(beliefModelService.listContextConcepts());
        Optional<Concept> currentContext = beliefModelService.getContext();
        virtualModelComponent.setSelectedContext(currentContext.orElse(null));
        virtualModelComponent.getCurrentStateComboBox().addValueChangeListener(new CurrentStateListener());

		virtualModelComponent.getLogArea().setReadOnly(true);

		setContent(virtualModelComponent);
	}

    public void updateCurrentState() {
        Optional<Concept> currentContext = beliefModelService.getContext();
        virtualModelComponent.setSelectedContext(currentContext.orElse(null));
    }

	private void fillBeliefListSelect(ListSelect beliefListSelect) {
		for (Concept concept : conceptRepository.findAll()) {
			beliefListSelect.addItem(concept.getName());
		}
		beliefListSelect.setNullSelectionAllowed(true);
		beliefListSelect.setMultiSelect(true);
	}

	private void addLogMessage(String message) {
		TextArea logArea = virtualModelComponent.getLogArea();
		String newValue = logArea.getValue() + message + '\n';
		logArea.setReadOnly(false);
		logArea.setValue(newValue);
		logArea.setReadOnly(true);
	}

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = VirtualModelUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

	private class UpdateWordnetComboboxRunnable implements Runnable {
		private final ComboBox wordnetComboBox;
		private final CheckBox wordnetCheckBox;

		public UpdateWordnetComboboxRunnable(ComboBox wordnetComboBox, CheckBox wordnetCheckBox) {
			this.wordnetComboBox = wordnetComboBox;
			this.wordnetCheckBox = wordnetCheckBox;
		}

		@Override
		public void run() {
			SortedSet<String> words = wordnetAccess.listWords();
			final Container c = new IndexedContainer();
			for (String word : words) {
				c.addItem(word);
			}
			access(() -> {
                wordnetComboBox.setContainerDataSource(c);
                wordnetCheckBox.setEnabled(true);
            });
		}
	}

    private class CurrentStateListener implements Property.ValueChangeListener {
        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            Optional<Concept> selectedContext = virtualModelComponent.getSelectedContext();
            if (selectedContext.isPresent()) {
                beliefModelService.setContext(selectedContext.get().getName());
            }
        }
    }

    private class SynchronousLogAreaLogger implements MentalWorldEngine.Logger, Serializable {

		@Override
		public void debug(String message) {
			if (applicationSettings.getBoolean(DEBUG_ON_SCREEN_PROPERTY, false)) {
				addLogMessage("   debug: " + message);
			}
			if (applicationSettings.getBoolean(DEBUG_IN_LOG_PROPERTY, false)) {
				java.util.logging.Logger.getLogger("Engine debug").info(message);
			}
		}

		@Override
		public void info(String message) {
			addLogMessage(message);
		}

		@Override
		public void warning(String message) {
			addLogMessage("Warning: " + message);
		}
    }
}
