/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.node;

import selemca.epistemics.data.entity.Association;

public interface InsecurityDeriverNode extends DeriverNode, ActionNode {
    /**
     * Insecurity about the given association
     */
    void insecurity(Association association);

    /**
     * Insecutity about current concept in working memory
     */
    void insecurity();
}
