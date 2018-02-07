/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.node;

import selemca.epistemics.data.entity.Association;

public interface ChangeConceptDeriverNode extends DeriverNode, ActionNode {
    /**
     * Change the given concept according to the given realistic contribution
     */
    void changeConcept(Association association, boolean isMetaphor);
}
