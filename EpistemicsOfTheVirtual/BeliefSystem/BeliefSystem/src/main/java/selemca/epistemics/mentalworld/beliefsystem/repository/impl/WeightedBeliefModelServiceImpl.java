/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.repository.impl;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections15.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.graph.GraphBuilder;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.BeliefModelService;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.WeightedBeliefModelService;

import java.util.*;

@Component("weightedBeliefModelService")
public class WeightedBeliefModelServiceImpl implements WeightedBeliefModelService {
    public static final Transformer<Association, Double> ASSOCIATION_WEIGHT_TRANSFORMER = association -> 1 - association.getTruthValue();

    @Autowired
    BeliefModelService delegateBeliefModelService;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Override
    public Set<Association> listAssociations(Concept concept, double minimumTruthValue) {
        return getWeightedVicinity(getGraph(), concept, minimumTruthValue);
    }

    @Override
    public Set<Concept> listAssociationConcepts(Concept concept, double minimumTruthValue) {
        return getWeightedVicinityConcepts(getGraph(), concept, minimumTruthValue);
    }

    private Graph<Concept, Association> getGraph() {
        Collection<Concept> concepts = conceptRepository.findAll();
        Collection<Association> associations = associationRepository.findAll();
        return new GraphBuilder(concepts, associations).build();
    }

    private Set<Association> getWeightedVicinity(Graph<Concept, Association> beliefSystemGraph, Concept concept, double minimumTruthValue) {
        double maxDistance = convertTruthValueToWeight(minimumTruthValue);

        DijkstraDistance<Concept, Association> dijkstraDistance = new DijkstraDistance<>(beliefSystemGraph, ASSOCIATION_WEIGHT_TRANSFORMER);
        dijkstraDistance.setMaxDistance(maxDistance);
        Map<Concept, Number> distanceMap = dijkstraDistance.getDistanceMap(concept);
        Set<Association> associations = new HashSet<>();
        for (Concept related : distanceMap.keySet()) {
            double weight = distanceMap.get(related).doubleValue();
            associations.add(new Association(concept, related, convertWeightToTruthValue(weight)));
        }
        return associations;
    }

    private Set<Concept> getWeightedVicinityConcepts(Graph<Concept, Association> beliefSystemGraph, Concept concept, double minimumTruthValue) {
        double maxDistance = convertTruthValueToWeight(minimumTruthValue);

        DijkstraDistance<Concept, Association> dijkstraDistance = new DijkstraDistance<>(beliefSystemGraph, ASSOCIATION_WEIGHT_TRANSFORMER);
        dijkstraDistance.setMaxDistance(maxDistance);
        return dijkstraDistance.getDistanceMap(concept).keySet();
    }

    private double convertWeightToTruthValue(double weight) {
        return 1.0 - weight;
    }
    private double convertTruthValueToWeight(double weight) {
        return 1.0 - weight;
    }

    @Override
    public void cascadingDelete(Concept concept) {
        delegateBeliefModelService.cascadingDelete(concept);
    }

    @Override
    public Set<Association> listAssociations(Concept concept) {
        return delegateBeliefModelService.listAssociations(concept);
    }

    @Override
    public Optional<Association> getAssociation(Concept concept1, Concept concept2) {
        return delegateBeliefModelService.getAssociation(concept1, concept2);
    }

    @Override
    public void fullSave(Association association) {
        delegateBeliefModelService.fullSave(association);
    }

    @Override
    public List<AssociationMeta> getAssociationMeta(Concept concept1, Concept concept2) {
        return delegateBeliefModelService.getAssociationMeta(concept1, concept2);
    }

    @Override
    public Optional<String> getAssociationType(Concept concept1, Concept concept2) {
        return delegateBeliefModelService.getAssociationType(concept1, concept2);
    }

    @Override
    public void setAssociationType(Concept concept1, Concept concept2, String relationType) {
        delegateBeliefModelService.setAssociationType(concept1, concept2, relationType);
    }

    @Override
    public void setContext(String context) {
        delegateBeliefModelService.setContext(context);
    }

    @Override
    public Optional<Concept> getContext() {
        return delegateBeliefModelService.getContext();
    }

    @Override
    public Set<Concept> listContextConcepts() {
        return delegateBeliefModelService.listContextConcepts();
    }

    @Override
    public void setConceptContextState(Concept concept, boolean isContext) {
        delegateBeliefModelService.setConceptContextState(concept, isContext);
    }

    @Override
    public boolean isContextConcept(Concept concept) {
        return delegateBeliefModelService.isContextConcept(concept);
    }

    @Override
    public void eraseAll() {
        delegateBeliefModelService.eraseAll();
    }
}
