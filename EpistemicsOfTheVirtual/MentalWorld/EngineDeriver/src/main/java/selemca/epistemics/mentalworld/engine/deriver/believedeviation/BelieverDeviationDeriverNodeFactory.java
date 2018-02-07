/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.believedeviation;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.factory.DeriverNodeFactory;
import selemca.epistemics.mentalworld.engine.node.BelieverDeviationDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

@Component
public class BelieverDeviationDeriverNodeFactory implements DeriverNodeFactory<BelieverDeviationDeriverNode> {
    private static final String CONFIGURATION_NAME = "believerDeviationDeriver.default";

    @Autowired
    private Configuration applicationSettings;

    @Override
    public Class<BelieverDeviationDeriverNode> getDeriverNodeClass() {
        return BelieverDeviationDeriverNode.class;
    }

    @Override
    public String getName() {
        return CONFIGURATION_NAME;
    }

    @Override
    public BelieverDeviationDeriverNode createDeriverNode(WorkingMemory workingMemory, MentalWorldEngine.Logger logger) {
        return new DefaultBelieverDeviationDeriverNodeImpl(workingMemory, logger, applicationSettings);
    }
}
