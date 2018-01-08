/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.service;

import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;

import java.util.Set;

/**
 * Extension of the BeliefModelService for querying (in)direct ui at a given distance.
 */
public interface WeightedBeliefModelService extends BeliefModelService {

    /**
     * Lists ui for the given concept with truth value greater or equal than the given minimumTruthValue.
     * That is all ui that have the concept as either first or second concept.
     * Returned set may be empty but is never null.
     */
    public Set<Association> listAssociations(Concept concept, double minimumTruthValue);

    /**
     * Lists associated concepts for the given concept with truth value greater or equal than the given minimumTruthValue.
     * That is all ui that have the concept as either first or second concept.
     * Returned set may be empty but is never null.
     * Functionally this is the same as calling the listAssociations(Concept, double) method and extracting
     * the associated concepts. However, implementations are free to implement this more efficiently.
     */
    public Set<Concept> listAssociationConcepts(Concept concept, double minimumTruthValue);
}
