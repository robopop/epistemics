/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.context;

import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.factory.DeriverNodeFactory;
import selemca.epistemics.mentalworld.engine.node.ContextMatchDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

/**
 * Created by henrizwols on 26-02-15.
 */
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
    public ContextMatchDeriverNode createDeriverNode(WorkingMemory workingMemory, Graph<Concept, Association> beliefSystemGraph, MentalWorldEngine.Logger logger) {
        return new DefaultContextMatchDeriverNodeImpl(beliefModelService, beliefSystemGraph, workingMemory, logger, applicationSettings);
    }
}
