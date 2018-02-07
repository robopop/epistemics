/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.insecurity;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import org.apache.commons.configuration.Configuration;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.graph.ConceptGraph;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.node.InsecurityDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static selemca.epistemics.mentalworld.engine.config.EngineConfig.BELIEF_SYSTEM_GRAPH;
import static selemca.epistemics.mentalworld.engine.deriver.insecurity.InsecurityDeriverNodeSettingsProvider.*;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.ASSOCIATION;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.CATEGORY_MATCH;

public class DefaultInsecurityDeriverNodeImpl implements InsecurityDeriverNode {
    final int INSECURITY_DIRECT_ASSOCIATION_MODIFICATION_PERCENTAGE_DEFAULT = 20;
    final double INSECURITY_CONVERSE_TO_VALUE_DEFAULT = 0.5;

    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;
    private final ConceptRepository conceptRepository;
    private final AssociationRepository associationRepository;

    private final int insecurityDirectAssociationModificationPercentage;
    private final double insecurityConverseToTarget;

    private final DijkstraShortestPath dijkstraShortestPath;

    public DefaultInsecurityDeriverNodeImpl(WorkingMemory workingMemory, MentalWorldEngine.Logger logger, ConceptRepository conceptRepository, AssociationRepository associationRepository, Configuration applicationSettings) {
        this.workingMemory = workingMemory;
        this.logger = logger;
        this.conceptRepository = conceptRepository;
        this.associationRepository = associationRepository;
        insecurityDirectAssociationModificationPercentage = applicationSettings.getInt(INSECURITY_DIRECT_ASSOCIATIONS_MODIFICATION_PERCENTAGE, INSECURITY_DIRECT_ASSOCIATION_MODIFICATION_PERCENTAGE_DEFAULT);
        insecurityConverseToTarget = applicationSettings.getDouble(INSECURITY_CONVERSE_TO_VALUE, INSECURITY_CONVERSE_TO_VALUE_DEFAULT);
        this.dijkstraShortestPath = new DijkstraShortestPath<>(getBeliefSystemGraph());
    }

    @Override
    public void apply() {
        workingMemory.getOptional(ASSOCIATION)
            .map(nonVoidConsumer(this::insecurity))
            .orElseGet(nonVoid(this::insecurity));
    }

    public <T> Function<T,Object> nonVoidConsumer(Consumer<T> consumer) {
        return t -> {
            consumer.accept(t);
            return null;
        };
    }

    public Supplier<Object> nonVoid(Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }

    @Override
    public void insecurity() {
        CategoryMatch categoryMatch = workingMemory.get(CATEGORY_MATCH);
        Concept bestFit = categoryMatch.getConcept();
        for (Concept contributor : categoryMatch.getContributors()) {
            insecurity(bestFit, contributor, categoryMatch.getContributorScore(contributor));
        }
    }

    @Override
    public void insecurity(Association association) {
        insecurity(association.getConcept1(), association.getConcept2(), association.getTruthValue());

    }

    public void insecurity(Concept concept1, Concept concept2, double truthValue) {
        Optional<Association> associationOptional = getAssociation(concept1, concept2);
        Association graphAssociation = associationOptional.orElse(createAssociation(concept1, concept2, truthValue));
        modifyAssociation(graphAssociation, insecurityConverseToTarget, insecurityDirectAssociationModificationPercentage);
        logger.debug(String.format("Deterred association(s) from %s to %s", graphAssociation.getConcept1().getName(), graphAssociation.getConcept2().getName()));

    }

    private Association createAssociation(Concept concept1, Concept concept2, double truthValue) {
        if (concept1.getName().compareToIgnoreCase(concept2.getName()) > 0) {
            Concept swapHelper = concept1;
            concept1 = concept2;
            concept2 = swapHelper;
        }
        return new Association(concept1, concept2, truthValue);
    }

    private Optional<Association> getAssociation(Concept concept1, Concept concept2) {
        ConceptGraph beliefSystemGraph = getBeliefSystemGraph();
        Association result = null;
        List<Association> edges = new ArrayList<>();
        edges.addAll(beliefSystemGraph.getInEdges(concept1));
        edges.addAll(beliefSystemGraph.getOutEdges(concept1));
        for (Association association : edges) {
            if (beliefSystemGraph.getOpposite(concept1, association).equals(concept2)) {
                result = association;
                break;
            }
        }
        return Optional.ofNullable(result);
    }

    private void modifyAssociation(Association association, double target, int percentage) {
        double truthValue = convertToValue(association.getTruthValue(), target, percentage);
        association.setTruthValue(truthValue);
        associationRepository.save(association);
    }

    private double convertToValue(double truthValue, double target, int percentage) {
        double modifier = percentage / 100.0;
        if (truthValue < target) {
            truthValue += (target - truthValue) * modifier;
        } else if (truthValue > target) {
            truthValue -= (truthValue - target) * modifier;
        }
        return truthValue;
    }

    private ConceptGraph getBeliefSystemGraph() {
        return workingMemory.get(BELIEF_SYSTEM_GRAPH);
    }
}
