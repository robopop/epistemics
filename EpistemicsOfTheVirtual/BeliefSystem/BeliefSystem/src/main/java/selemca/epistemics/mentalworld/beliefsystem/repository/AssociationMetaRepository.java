/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.repository;

import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;

import java.util.List;

public interface AssociationMetaRepository extends BaseRepository<AssociationMeta, Long> {
    List<AssociationMeta> findByConcept1AndConcept2(Concept concept1, Concept concept2);
}
