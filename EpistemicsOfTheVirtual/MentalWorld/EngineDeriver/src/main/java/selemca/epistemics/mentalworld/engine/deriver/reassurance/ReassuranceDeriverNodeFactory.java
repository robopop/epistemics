/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.reassurance;

import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.factory.DeriverNodeFactory;
import selemca.epistemics.mentalworld.engine.node.ReassuranceDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

/**
 * Created by henrizwols on 27-02-15.
 */
@Component
public class ReassuranceDeriverNodeFactory implements DeriverNodeFactory<ReassuranceDeriverNode> {
    private static final String CONFIGURATION_NAME = "reassuranceDeriver.default";

    @Autowired
    private Configuration applicationSettings;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Override
    public Class<ReassuranceDeriverNode> getDeriverNodeClass() {
        return ReassuranceDeriverNode.class;
    }

    @Override
    public String getName() {
        return CONFIGURATION_NAME;
    }

    @Override
    public ReassuranceDeriverNode createDeriverNode(WorkingMemory workingMemory, Graph<Concept, Association> beliefSystemGraph, MentalWorldEngine.Logger logger) {
        return new DefaultReassuranceDeriverNodeImpl(workingMemory, beliefSystemGraph, logger, conceptRepository, associationRepository, applicationSettings);
    }
}
