/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.repository;

import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.data.entity.ConceptMeta;

import java.util.List;
import java.util.Optional;

public interface ConceptMetaRepository extends BaseRepository<ConceptMeta, Long> {
    List<ConceptMeta> findByConcept(Concept concept);
    List<ConceptMeta> findByRelationAndValue(String relation, String value);
    Optional<ConceptMeta> findByConceptAndRelationAndValue(Concept concept, String relation, String value);
}
