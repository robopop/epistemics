/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.node;

import selemca.epistemics.data.entity.Concept;

import java.util.Collection;

/**
 * Epistemics of the virtual node: believe deviation tolerant
 * Decides if willing to deviate from beliefs.
 * Deviation tollerance is not a simple yes or no. System may be willing to deviate for parts
 * of the observation and for other parts not.
 */
public interface BelieverDeviationDeriverNode extends DeriverNode {
    /**
     * Returns true iff the system is willing to deviate from its beliefs.
     */
    boolean isDeviationTolerant();
    /**
     * Gets the observed features for which the system is willing to deviate from current belief.
     * Returned collection may be empty but is never null.
     */
    Iterable<Concept> getWillingToDeviateContributors();

    /**
     * Gets the observed features for which the system is unwilling to deviate from current belief.
     * Returned collection may be empty but is never null.
     */
    Iterable<Concept> getUnwillingToDeviateContributors();
}
