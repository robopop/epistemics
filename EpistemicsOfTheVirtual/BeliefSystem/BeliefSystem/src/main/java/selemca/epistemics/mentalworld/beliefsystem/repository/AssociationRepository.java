/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.repository;

import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationPK;
import selemca.epistemics.data.entity.Concept;

import java.util.List;
import java.util.Optional;

public interface AssociationRepository extends BaseRepository<Association, AssociationPK> {
    List<Association> findByConcept1(Concept concept);
    List<Association> findByConcept2(Concept concept);
    Optional<Association> findByConcept1AndConcept2(Concept concept1, Concept concept2);
}
