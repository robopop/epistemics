package selemca.epistemics.mentalworld.webapp.ui;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;

@SuppressWarnings("serial")
class OfferObservationButtonListener implements ClickListener {
	private final MentalWorldEngine virtualModelEngine;
    private final VirtualModelUI virtualModelUI;
	private final ObservationComponent observationComponent;
	private final MentalWorldEngine.Logger logger;

	public OfferObservationButtonListener(
			MentalWorldEngine virtualModelEngine, VirtualModelUI virtualModelUI,
            ObservationComponent observationComponent,
			MentalWorldEngine.Logger logger) {
		super();
		this.virtualModelEngine = virtualModelEngine;
        this.virtualModelUI = virtualModelUI;
		this.observationComponent = observationComponent;
		this.logger = logger;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Set<String> observationFeatures = getObservationFeatures();
		logFeatures(observationFeatures, logger);
		virtualModelEngine.acceptObservation(observationFeatures, logger);
		logger.info("");
        virtualModelUI.updateCurrentState();
	}

	private void logFeatures(Set<String> observationFeatures, MentalWorldEngine.Logger logger) {
		logger.info(String.format("Features: %s", observationFeatures));
	}

	private Set<String> getObservationFeatures() {
		Set<String> observationFeatures = new HashSet<>();
		observationFeatures.addAll(getBeliefListSelectValues());
		if (observationComponent.getWordnetCheckBox().getValue()) {
			String newConcept = (String) observationComponent.getWordnetComboBox().getValue();
			if (newConcept != null) {
                if (observationComponent.getPluralCheckbox().getValue()) {
                    newConcept += "s";
                }
				observationFeatures.add(newConcept);
			}
		}
		return observationFeatures;
	}

    @SuppressWarnings("unchecked")
    private Set<String> getBeliefListSelectValues() {
        return (Set<String>) observationComponent.getBeliefListSelect().getValue();
    }
}
