/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.changeconcept;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.factory.DeriverNodeFactory;
import selemca.epistemics.mentalworld.engine.node.ChangeConceptDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

@Component
public class ChangeConceptDeriverNodeFactory implements DeriverNodeFactory<ChangeConceptDeriverNode> {
    private static final String CONFIGURATION_NAME = "changeConceptDeriver.default";

    @Autowired
    private Configuration applicationSettings;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private BeliefModelService beliefModelService;

    @Override
    public Class<ChangeConceptDeriverNode> getDeriverNodeClass() {
        return ChangeConceptDeriverNode.class;
    }

    @Override
    public String getName() {
        return CONFIGURATION_NAME;
    }

    @Override
    public ChangeConceptDeriverNode createDeriverNode(WorkingMemory workingMemory, MentalWorldEngine.Logger logger) {
        return new DefaultChangeConceptDeriverNodeImpl(workingMemory, logger, associationRepository, beliefModelService, applicationSettings);
    }
}
