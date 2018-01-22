/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.impl;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.MentalWorldEngineState;
import selemca.epistemics.mentalworld.engine.accept.Engine;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;
import selemca.epistemics.mentalworld.registry.DeriverNodeProviderRegistry;
import selemca.epistemics.mentalworld.registry.MetaphorProcessorRegistry;

import java.util.*;

@Component("mentalWorldEngine")
public class MentalWorldEngineImpl implements MentalWorldEngine {
    public static final String SUBJECT_NAME = "subject";
    public static final int MAXIMUM_TRAVERSALS_DEFAULT = 1;

    @Autowired
    private BeliefModelService beliefModelService;

    @Autowired
    private MetaphorProcessorRegistry metaphorProcessorRegistry;

    @Autowired
    private DeriverNodeProviderRegistry deriverNodeProviderRegistry;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private Configuration applicationSettings;

    public BeliefModelService getBeliefModelService() {
        return beliefModelService;
    }

    public MetaphorProcessorRegistry getMetaphorProcessorRegistry() {
        return metaphorProcessorRegistry;
    }

    public DeriverNodeProviderRegistry getDeriverNodeProviderRegistry() {
        return deriverNodeProviderRegistry;
    }

    public ConceptRepository getConceptRepository() {
        return conceptRepository;
    }

    public AssociationRepository getAssociationRepository() {
        return associationRepository;
    }

    public Configuration getApplicationSettings() {
        return applicationSettings;
    }

    @Override
    public void acceptObservation(Set<String> observationFeatures, Logger logger) {
        Optional<Concept> contextOptional = beliefModelService.getContext();
        if (contextOptional.isPresent()) {
            VirtualModelEngineState virtualModelEngineState = new VirtualModelEngineState(this, contextOptional.get(), observationFeatures, logger);

            virtualModelEngineState.acceptObservation();
        } else {
            logger.info("There is no context. We are mentally blind");
        }
    }

    @Override
    public boolean acceptObservation(Set<String> observationFeatures, Engine engineSettings, Logger logger) {
        Optional<Concept> contextOptional = beliefModelService.getContext();
        if (contextOptional.isPresent()) {
            MentalWorldEngineState mentalWorldModelEngineState = createState(logger);
            WorkingMemory workingMemory = mentalWorldModelEngineState.getWorkingMemory();
            workingMemory.setEngineSettings(engineSettings);
            workingMemory.setObservationFeatures(observationFeatures);
            workingMemory.setNewContext(contextOptional.get());

            mentalWorldModelEngineState.acceptObservation();
            return mentalWorldModelEngineState.isObservationAccepted();
        } else {
            logger.info("There is no context. We are mentally blind");
            return false;
        }
    }

    @Override
    public MentalWorldEngineState createState(Logger logger) {
        return new VirtualModelEngineState(this, new WorkingMemory(), logger);
    }
}
