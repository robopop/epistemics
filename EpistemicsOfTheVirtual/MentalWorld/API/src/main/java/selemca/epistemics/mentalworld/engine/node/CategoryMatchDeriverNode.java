/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.node;

import java.util.Collection;

/**
 * Epistemics of the virtual node: category match
 * Finds concepts that match observed features
 */
public interface CategoryMatchDeriverNode extends DeriverNode, DecisionNode {
    /**
     * Finds a matching concept for the observed features, stores it in working memory
     * @return true iff the found match matches all observed features and is within context.
     */
    boolean categoryMatch(Iterable<String> precludeConcepts);
}
