/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.category;

import org.apache.commons.configuration.Configuration;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.graph.ConceptGraph;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.category.CategoryMatcher;
import selemca.epistemics.mentalworld.engine.deriver.util.GraphUtil;
import selemca.epistemics.mentalworld.engine.node.CategoryMatchDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static selemca.epistemics.mentalworld.engine.config.EngineConfig.BELIEF_SYSTEM_GRAPH;
import static selemca.epistemics.mentalworld.engine.deriver.context.ContextDeriverNodeSettingsProvider.CONTEXT_ASSOCIATION_MAXIMUM_DISTANCE;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.CATEGORY_MATCH;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.OBSERVATION_FEATURES;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.PRECLUDE_CONCEPTS;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.size;

public class DefaultCategoryMatchDeriverNodeImpl implements CategoryMatchDeriverNode {
    final double CONTEXT_ASSOCIATION_MAXIMUM_DISTANCE_DEFAULT = 1.0;

    private final WorkingMemory workingMemory;
    private final CategoryMatcher categoryMatcher;
    private final MentalWorldEngine.Logger logger;
    private BeliefModelService beliefModelService;
    private double contextAssociationMaximumDistance;

    public DefaultCategoryMatchDeriverNodeImpl(BeliefModelService beliefModelService, WorkingMemory workingMemory, CategoryMatcher categoryMatcher, MentalWorldEngine.Logger logger, Configuration applicationSettings) {
        this.workingMemory = workingMemory;
        this.categoryMatcher = categoryMatcher;
        this.logger = logger;
        this.beliefModelService = beliefModelService;
        contextAssociationMaximumDistance = applicationSettings.getDouble(CONTEXT_ASSOCIATION_MAXIMUM_DISTANCE, CONTEXT_ASSOCIATION_MAXIMUM_DISTANCE_DEFAULT);
    }

    @Override
    public boolean decide() {
        Iterable<String> precludeConcepts = workingMemory.getAll(PRECLUDE_CONCEPTS);
        Iterable<String> observationFeatures = workingMemory.getAll(OBSERVATION_FEATURES);
        ConceptGraph beliefSystemGraph = workingMemory.get(BELIEF_SYSTEM_GRAPH);
        Optional<CategoryMatch> categoryMatchOptional = categoryMatcher.findMatch(beliefSystemGraph, observationFeatures, precludeConcepts, logger);
        return categoryMatchOptional
                .map(foundMatch -> {
                    workingMemory.set(CATEGORY_MATCH, foundMatch);
                    logger.debug(foundMatch.toString());

                    boolean match = foundMatch.getContributors().size() == size(observationFeatures);
                    match &= withinContext(foundMatch);
                    match &= allObservationsWithinReality();
                    logger.debug("Match is " + (match ? "valid." : "invalid."));
                    return match;
                })
                .orElseGet(() -> {
                    logger.debug("No match found.");
                    return false;
                });
    }

    @Override
    public boolean categoryMatch(Iterable<String> precludeConcepts) {
        workingMemory.set(PRECLUDE_CONCEPTS, precludeConcepts);
        return decide();
    }

    private boolean withinContext(CategoryMatch categoryMatch) {
        ConceptGraph beliefSystemGraph = workingMemory.get(BELIEF_SYSTEM_GRAPH);
        return beliefModelService.getContext()
            .map(context -> {
                double distance = new GraphUtil().getDistance(beliefSystemGraph, context, categoryMatch.getConcept());
                boolean withinContext = (distance <= contextAssociationMaximumDistance);
                logger.debug(String.format("Concept %s is %splausible within context %s (distance %s)", categoryMatch.getConcept(), withinContext ? "": "not ", context, distance));
                return withinContext;
            })
            .orElseGet(() -> {
                logger.debug("No context is set");
                return false;
            });
    }

    private boolean allObservationsWithinReality() {
        CategoryMatch categoryMatch = workingMemory.get(CATEGORY_MATCH);
        Set<Concept> contributors = categoryMatch.getContributors();

        Set<Concept> withinReality = contributors.stream()
                .filter(categoryMatch::withinReality)
                .collect(Collectors.toSet());

        Set<Concept> notWithinReality = new HashSet<>(contributors);
        notWithinReality.removeAll(withinReality);

        boolean result = notWithinReality.isEmpty();

        if (!withinReality.isEmpty()) {
            logger.debug(String.format("Within reality: %s", withinReality));
        }
        if (!notWithinReality.isEmpty()) {
            logger.debug(String.format("Not within reality: %s", notWithinReality));
        }

        return result;
    }
}
