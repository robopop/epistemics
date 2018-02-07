/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.context;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.factory.DeriverNodeFactory;
import selemca.epistemics.mentalworld.engine.node.ContextMatchDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

@Component
public class ContextDeriverNodeFactory implements DeriverNodeFactory<ContextMatchDeriverNode> {
    private static final String CONFIGURATION_NAME = "contextMatchDeriver.default";

    @Autowired
    private BeliefModelService beliefModelService;

    @Autowired
    private Configuration applicationSettings;

    @Override
    public Class<ContextMatchDeriverNode> getDeriverNodeClass() {
        return ContextMatchDeriverNode.class;
    }

    @Override
    public String getName() {
        return CONFIGURATION_NAME;
    }

    @Override
    public ContextMatchDeriverNode createDeriverNode(WorkingMemory workingMemory, MentalWorldEngine.Logger logger) {
        return new DefaultContextMatchDeriverNodeImpl(beliefModelService, workingMemory, logger, applicationSettings);
    }
}
