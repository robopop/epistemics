/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.insecurity;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.factory.DeriverNodeFactory;
import selemca.epistemics.mentalworld.engine.node.InsecurityDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

@Component
public class InsecurityDeriverNodeFactory implements DeriverNodeFactory<InsecurityDeriverNode> {
    private static final String CONFIGURATION_NAME = "insecurityDeriver.default";

    @Autowired
    private Configuration applicationSettings;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Override
    public Class<InsecurityDeriverNode> getDeriverNodeClass() {
        return InsecurityDeriverNode.class;
    }

    @Override
    public String getName() {
        return CONFIGURATION_NAME;
    }

    @Override
    public InsecurityDeriverNode createDeriverNode(WorkingMemory workingMemory, MentalWorldEngine.Logger logger) {
        return new DefaultInsecurityDeriverNodeImpl(workingMemory, logger, conceptRepository, associationRepository, applicationSettings);
    }
}
