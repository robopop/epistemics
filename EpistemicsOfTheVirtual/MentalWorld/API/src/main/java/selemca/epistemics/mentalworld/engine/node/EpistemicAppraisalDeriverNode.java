/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.node;

import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;

import java.util.Collection;

/**
 * Epistemics of the virtual node: epistemic appraisal
 */
public interface EpistemicAppraisalDeriverNode extends DeriverNode {
    Concept getCategory();

    Collection<Association> getRealisticContributions();

    Collection<Association> getUnrealisticContributions();
}
