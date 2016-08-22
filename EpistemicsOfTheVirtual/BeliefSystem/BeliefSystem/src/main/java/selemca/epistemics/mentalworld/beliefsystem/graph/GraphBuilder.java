/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.graph;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;

import java.util.Collection;

public class GraphBuilder {
    private final Collection<Concept> concepts;
    private final Collection<Association> associations;

    public GraphBuilder(Collection<Concept> concepts, Collection<Association> associations) {
        this.concepts = concepts;
        this.associations = associations;
    }

    public Graph<Concept, Association> build() {
        UndirectedSparseGraph<Concept, Association> graph = new UndirectedSparseGraph<>();
        for (Concept concept : concepts) {
            graph.addVertex(concept);
        }
        for (Association association : associations) {
            graph.addEdge(association, association.getConcept1(), association.getConcept2());
        }

        return graph;
    }
}
