/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.factory;

import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.node.DeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

public interface DeriverNodeFactory<D extends DeriverNode> {
    Class<D> getDeriverNodeClass();
    String getName();
    D createDeriverNode(WorkingMemory workingMemory, MentalWorldEngine.Logger logger);
}
