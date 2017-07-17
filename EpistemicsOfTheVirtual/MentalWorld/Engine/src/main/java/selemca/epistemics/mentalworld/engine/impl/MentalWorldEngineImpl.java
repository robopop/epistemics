/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;

import java.util.*;

@Component("mentalWorldEngine")
public class MentalWorldEngineImpl implements MentalWorldEngine {
    public static final String SUBJECT_NAME = "subject";
    public static final int MAXIMUM_TRAVERSALS_DEFAULT = 1;

    @Autowired
    private BeliefModelService beliefModelService;

    @Override
    public void acceptObservation(Set<String> observationFeatures, Logger logger) {
        Optional<Concept> contextOptional = beliefModelService.getContext();
        if (contextOptional.isPresent()) {
            VirtualModelEngineState virtualModelEngineState = new VirtualModelEngineState(contextOptional.get(), observationFeatures, logger);

            virtualModelEngineState.acceptObservation();
        } else {
            logger.info("There is no context. We are mentally blind");
        }
    }


}
