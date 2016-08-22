/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.reassurance;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.configuration.Configuration;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.node.ReassuranceDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import java.util.ArrayList;
import java.util.List;

import static selemca.epistemics.mentalworld.engine.deriver.reassurance.ReassuranceDeriverNodeSettingsProvider.*;

/**
 * Created by henrizwols on 26-02-15.
 */
public class DefaultReassuranceDeriverNodeImpl implements ReassuranceDeriverNode {
    final int REASSURENCE_DIRECT_ASSOCIATION_MODIFICATION_PERCENTAGE_DEFAULT = 20;
    final int REASSURENCE_INDIRECT_ASSOCIATIONS_MODIFICATION_PERCENTAGE_DEFAULT = 5;

    private final WorkingMemory workingMemory;
    private final Graph<Concept, Association> beliefSystemGraph;
    private final MentalWorldEngine.Logger logger;
    private final ConceptRepository conceptRepository;
    private final AssociationRepository associationRepository;

    private final int reassuranceDirectAssociationModificationPercentage;
    private final int reassuranceOtherAssociationsModificationPercentage;

    private final DijkstraShortestPath dijkstraShortestPath;

    public DefaultReassuranceDeriverNodeImpl(WorkingMemory workingMemory, Graph<Concept, Association> beliefSystemGraph, MentalWorldEngine.Logger logger, ConceptRepository conceptRepository, AssociationRepository associationRepository, Configuration applicationSettings) {
        this.workingMemory = workingMemory;
        this.beliefSystemGraph = beliefSystemGraph;
        this.logger = logger;
        this.conceptRepository = conceptRepository;
        this.associationRepository = associationRepository;
        reassuranceDirectAssociationModificationPercentage = applicationSettings.getInt(REASSURENCE_DIRECT_ASSOCIATION_MODIFICATION_PERCENTAGE, REASSURENCE_DIRECT_ASSOCIATION_MODIFICATION_PERCENTAGE_DEFAULT);
        reassuranceOtherAssociationsModificationPercentage = applicationSettings.getInt(REASSURENCE_INDIRECT_ASSOCIATIONS_MODIFICATION_PERCENTAGE, REASSURENCE_INDIRECT_ASSOCIATIONS_MODIFICATION_PERCENTAGE_DEFAULT);
        this.dijkstraShortestPath = new DijkstraShortestPath(beliefSystemGraph);
    }

    @Override
    public void reassurance() {
        CategoryMatch categoryMatch = workingMemory.getCategoryMatch();
        Concept matchingConcept = categoryMatch.getConcept();
        for (Concept contributor : categoryMatch.getContributors()) {
            modifyAssociationPath(matchingConcept, contributor);
            logger.debug(String.format("Reassured association(s) from %s to %s", contributor.getName(), matchingConcept.getName()));
        }
//        reassureConcept(matchingConcept);
//        logger.debug("Reassured concept: " + matchingConcept);
    }

    private void modifyAssociationPath(Concept bestFit, Concept contributor) {
        int directPercentage = reassuranceDirectAssociationModificationPercentage;
        int othersPercentage = reassuranceOtherAssociationsModificationPercentage;
        double targetValue = 1.0;

        List<Association> path = new ArrayList<>(dijkstraShortestPath.getPath(bestFit, contributor));
        if (!path.isEmpty()) {
            Association first = path.remove(0);
            modifyAssociation(first, targetValue, directPercentage);
            for (Association association : path) {
                modifyAssociation(association, targetValue, othersPercentage);
            }
        }
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
}
