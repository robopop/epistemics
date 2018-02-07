/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.node;

import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;

/**
 * Epistemics of the virtual node: epistemic appraisal
 */
public interface EpistemicAppraisalDeriverNode extends DeriverNode, ActionNode {
    Concept getCategory();

    Iterable<Association> getRealisticContributions();

    Iterable<Association> getUnrealisticContributions();
}
