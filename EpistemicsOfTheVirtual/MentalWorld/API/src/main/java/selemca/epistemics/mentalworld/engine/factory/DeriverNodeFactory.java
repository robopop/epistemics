/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.factory;

import edu.uci.ics.jung.graph.Graph;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.node.DeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

/**
 * Created by henrizwols on 26-02-15.
 */
public interface DeriverNodeFactory<D extends DeriverNode> {
    Class<D> getDeriverNodeClass();
    String getName();
    D createDeriverNode(WorkingMemory workingMemory, Graph<Concept, Association> beliefSystemGraph, MentalWorldEngine.Logger logger);
}
