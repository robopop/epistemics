/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.matcher;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.category.CategoryMatcher;
import selemca.epistemics.mentalworld.engine.deriver.util.GraphUtil;
import selemca.epistemics.mentalworld.engine.realitycheck.RealityCheck;
import selemca.epistemics.mentalworld.registry.RealityCheckRegistry;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component("categoryMatcher.default")
public class CategoryMatcherImpl implements CategoryMatcher {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private RealityCheckRegistry realityCheckRegistry;

    @Override
    public Optional<CategoryMatch> findMatch(Graph<Concept, Association> beliefSystemGraph, Iterable<String> features, MentalWorldEngine.Logger logger) {
        return findMatch(beliefSystemGraph, features, Collections.emptyList(), logger);
    }

    @Override
    public Optional<CategoryMatch> findMatch(Graph<Concept, Association> beliefSystemGraph, Iterable<String> features, Collection<String> precludeConcepts, MentalWorldEngine.Logger logger) {
        Set<Concept> featureConcepts = getFeatureConcepts(beliefSystemGraph, features);
        Set<Concept> candidateMatches = getVicinity(beliefSystemGraph, featureConcepts);
        return findMatch(beliefSystemGraph, candidateMatches, featureConcepts, precludeConcepts, logger);
    }

    private Set<Concept> getFeatureConcepts(Graph<Concept, Association> beliefSystemGraph, Iterable<String> features) {
        Set<String> featureSet = StreamSupport.stream(features.spliterator(), false).collect(Collectors.toSet());
        Set<Concept> concepts = new HashSet<>();
        for (Concept concept : beliefSystemGraph.getVertices()) {
            if (featureSet.contains(concept.getName())) {
                concepts.add(concept);
            }
        }
        return concepts;
    }

    private Set<Concept> getVicinity(Graph<Concept, Association> beliefSystemGraph, Set<Concept> featureConcepts) {
        Set<Concept> neighbors = new HashSet<>();
        for (Concept feature : featureConcepts) {
            neighbors.addAll(beliefSystemGraph.getNeighbors(feature));
        }
        return neighbors;
    }

    private Optional<CategoryMatch> findMatch(Graph<Concept, Association> beliefSystemGraph, Collection<Concept> candidateMatches, Set<Concept> featureConcepts, Collection<String> precludeConcepts, MentalWorldEngine.Logger logger) {
        RealityCheck realityCheck = getRealityCheck();
        DijkstraShortestPath<Concept, Association> dijkstraDistance = new DijkstraShortestPath<>(beliefSystemGraph);

        SortedSet<CategoryMatchImpl> matches = new TreeSet<>(new CategoryScoreComparator());
        for (Concept cadidateMatch : candidateMatches) {
            if (!precludeConcepts.contains(cadidateMatch.getName())) {
                matches.add(match(dijkstraDistance, cadidateMatch, featureConcepts, realityCheck));
            }
        }
        if (matches.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(matches.last());
        }
    }

    private CategoryMatchImpl match(DijkstraShortestPath<Concept, Association> dijkstraDistance, Concept concept, Set<Concept> featureConcepts, RealityCheck realityCheck) {
        CategoryMatchImpl match = new CategoryMatchImpl(concept, realityCheck);
        GraphUtil graphUtil = new GraphUtil();
        for (Concept feature : featureConcepts) {
            double truthValue = graphUtil.getTruthValue(dijkstraDistance, concept, feature);
            match.addContribution(feature, truthValue);
        }
        return match;
    }

    private RealityCheck getRealityCheck() {
        Optional<RealityCheck> realityCheckOptional = realityCheckRegistry.getImplementation();
        return realityCheckOptional.orElseThrow(() -> new IllegalStateException("RealityCheck not present"));
    }

    /**
     * Find best scoring CategoryMatch. Not to be used with empty collection!
     */
    private CategoryMatchImpl getBestCategoryMatch(Collection<CategoryMatchImpl> categoryMatches) {
        CategoryMatchImpl[] categoryMatchesArray = categoryMatches.toArray(new CategoryMatchImpl[categoryMatches.size()]);
        Comparator<CategoryMatchImpl> comparator = new CategoryScoreComparator();
        Arrays.sort(categoryMatchesArray, comparator);
        return categoryMatchesArray[categoryMatchesArray.length - 1];
    }

    private class CategoryScoreComparator implements Comparator<CategoryMatchImpl> {
        @Override
        public int compare(CategoryMatchImpl m1, CategoryMatchImpl m2) {
            return Double.compare(m1.getMatchScore(), m2.getMatchScore());
        }
    }
}
