/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.category;

import edu.uci.ics.jung.graph.Graph;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;

import java.util.Collection;
import java.util.Optional;

public interface CategoryMatcher {
    Optional<CategoryMatch> findMatch(Graph<Concept, Association> beliefSystemGraph, Iterable<String> features, MentalWorldEngine.Logger logger);
    Optional<CategoryMatch> findMatch(Graph<Concept, Association> beliefSystemGraph, Iterable<String> features, Collection<String> precludeConcepts, MentalWorldEngine.Logger logger);
}
