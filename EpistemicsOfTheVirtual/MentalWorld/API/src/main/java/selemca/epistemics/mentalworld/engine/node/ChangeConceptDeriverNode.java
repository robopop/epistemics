/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.node;

import selemca.epistemics.data.entity.Association;

/**
 * Created by henrizwols on 26-02-15.
 */
public interface ChangeConceptDeriverNode extends DeriverNode {
    /**
     * Change the given concept according to the given realistic contribution
     */
    void changeConcept(Association association, boolean isMetaphor);
}
