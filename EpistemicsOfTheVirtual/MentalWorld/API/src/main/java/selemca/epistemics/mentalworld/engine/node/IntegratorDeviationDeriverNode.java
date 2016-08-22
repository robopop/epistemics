/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.node;

import selemca.epistemics.data.entity.Concept;

/**
 * Epistemics of the virtual node: intergrator deviation tolerant
 * Decides if willing to deviate from beliefs.
 * Deviation tollerance is not a simple yes or no. System may be willing to deviate for parts
 * of the observation and for other parts not.
 */
public interface IntegratorDeviationDeriverNode extends DeriverNode {
    /**
     * Returns true iff system is willing to deviate from current belief.
     * @param concept concept under scrutiny
     * @param contributor
     * @return
     */
    boolean isWillingToDeviate(Concept concept, Concept contributor);
}
