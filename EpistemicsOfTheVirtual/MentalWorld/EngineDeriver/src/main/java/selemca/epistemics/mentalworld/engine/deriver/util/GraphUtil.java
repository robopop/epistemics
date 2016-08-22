/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.util;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;

import java.util.List;
import java.util.Set;

/**
 * Created by henrizwols on 06-03-15.
 */
public class GraphUtil {
    private Set<Concept> getUnweightedVicinity(Graph<Concept, Association> beliefSystemGraph, Concept concept, int maxDistance) {
        DijkstraDistance dijkstraDistance = new DijkstraDistance<Concept, Association>(beliefSystemGraph);
        dijkstraDistance.setMaxDistance(maxDistance);
        return dijkstraDistance.getDistanceMap(concept).keySet();
    }

    public List<Association> getUnweightedShortestPath(Graph<Concept, Association> beliefSystemGraph, Concept concept1, Concept concept2) {
        DijkstraShortestPath<Concept,Association> beliefSystemDSP = new DijkstraShortestPath(beliefSystemGraph);
        return beliefSystemDSP.getPath(concept1, concept2);
    }

    public double getTruthValue(Graph<Concept, Association> beliefSystemGraph, Concept concept1, Concept concept2) {
        DijkstraShortestPath<Concept,Association> beliefSystemDSP = new DijkstraShortestPath(beliefSystemGraph);
        return getTruthValue(beliefSystemDSP, concept1, concept2);
    }

    public double getTruthValue(DijkstraShortestPath<Concept,Association> beliefSystemDSP, Concept concept1, Concept concept2) {
        List<Association> path = beliefSystemDSP.getPath(concept1, concept2);
        double result = 0.0;
        if (!path.isEmpty()) {
            result = 1.0;
            for (Association association : path) {
                result *= association.getTruthValue();
            }
        }
        return result;
    }

    public double getDistance(Graph<Concept, Association> beliefSystemGraph, Concept concept1, Concept concept2) {
        List<Association> path = getUnweightedShortestPath(beliefSystemGraph, concept1, concept2);
        double result = Double.MAX_VALUE;
        if (!path.isEmpty()) {
            result = 0.0;
            for (Association association : path) {
                result += (1.0 - association.getTruthValue());
            }
        }
        return result;
    }
}
