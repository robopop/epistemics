package selemca.epistemics.mentalworld.beliefsystem.service;

import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Facade for agggregate repository methods
 */
/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
public interface BeliefModelService {
    String CONTEXT_PROPERTY = "context";
    String CONCEPTMETA_RELATION_KINDOF = "kind_of";
    String CONCEPTMETA_VALUE_CONTEXT = "context";
    String RELATION_TYPE = "relationType";

    // CONCEPT

    /**
     * Delete the given concept and everything that depends on it: ui, metadata and ownstate.
     */
    void cascadingDelete(Concept concept);

    // ASSOCIATION

    /**
     * Lists ui for the given concepts. That is all ui that have the concept as either
     * first or second concept.
     * Returned set may be empty but is never null.
     */
    Set<Association> listAssociations(Concept concept);

    /**
     * Find an association between the given concepts. The order of the concepts is irrelevant.
     */
    Optional<Association> getAssociation(Concept concept1, Concept concept2);

    /**
     * Saves the given association. Also saves the referenced concepts if necessary.
     */
    void fullSave(Association association);

    /**
     * Find an association-meta between the given concepts. The order of the concepts is irrelevant.
     */
    List<AssociationMeta> getAssociationMeta(Concept concept1, Concept concept2);

    /**
     * Find the type (literal or figurative) of the association between the given concepts. The order of the concepts is irrelevant.
     */
    Optional<String> getAssociationType(Concept concept1, Concept concept2);

    /**
     * Set the type (literal or figurative) of the association between the given concepts.
     */
    void setAssociationType(Concept concept1, Concept concept2, String relationType);

    // CONTEXT

    /**
     * Sets the current context
     */
    void setContext(String context);

    /**
     * Resets the current context
     */
    void resetContext();

    /**
     * Gets the current context
     */
    Optional<Concept> getContext();

    /**
     * Lists all concepts that, according to their metadata, can act as context.
     * Returned set may be empty but is never null.
     */
    Set<Concept> listContextConcepts();

    /**
     * Returns true iff the given concept is marked a 'context' in the metadata
     */
    boolean isContextConcept(Concept concept);

    /**
     * Add or remove the 'context' metadata for the given concept.
     * If isContext is true the concept is a context. Add context metadata if not yet present.
     * If isContext is false the concept is not a context. Remove context metadata when present.
     */
    void setConceptContextState(Concept concept, boolean isContext);

    /**
     * Erases all data from Concept, ConceptMeta, Association, AssociationMeta and OwnState
     */
    void eraseAll();
}
